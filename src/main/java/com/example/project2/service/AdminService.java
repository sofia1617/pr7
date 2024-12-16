package com.example.project2.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final JdbcTemplate jdbcTemplate;

    public AdminService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Value("${logging.file.name}") // Считывает путь к файлу логов из настроек
    private String logFilePath;

    // Метод для получения логов
    public List<String> getLogs() {
        try {
            return Files.readAllLines(Paths.get(logFilePath));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении логов: " + e.getMessage(), e);
        }
    }

    /**
     * Экспорт всех таблиц в формат CSV.
     */
    public String exportAllToCSV() {
        StringBuilder csvBuilder = new StringBuilder();
        List<String> tables = getAllTableNames();

        for (String table : tables) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM " + table);
            csvBuilder.append("TABLE: ").append(table).append("\n");

            if (!rows.isEmpty()) {
                String headers = String.join(",", rows.get(0).keySet());
                csvBuilder.append(headers).append("\n");

                for (Map<String, Object> row : rows) {
                    String values = row.values().stream()
                            .map(value -> value != null ? value.toString() : "")
                            .collect(Collectors.joining(","));
                    csvBuilder.append(values).append("\n");
                }
            }
            csvBuilder.append("\n");
        }

        return csvBuilder.toString();
    }

    /**
     * Экспорт всех таблиц в формат SQL.
     */
    public String exportAllToSQL() {
        StringBuilder sqlBuilder = new StringBuilder();
        List<String> tables = getAllTableNames();

        for (String table : tables) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM " + table);

            for (Map<String, Object> row : rows) {
                StringBuilder columns = new StringBuilder();
                StringBuilder values = new StringBuilder();

                row.forEach((column, value) -> {
                    columns.append(column).append(",");
                    values.append(value != null ? "'" + value.toString().replace("'", "''") + "'" : "NULL").append(",");
                });

                sqlBuilder.append("INSERT INTO ").append(table)
                        .append(" (").append(columns.substring(0, columns.length() - 1)).append(") ")
                        .append("VALUES (").append(values.substring(0, values.length() - 1)).append(");")
                        .append("\n");
            }
        }

        return sqlBuilder.toString();
    }

    /**
     * Импорт всех таблиц из CSV-файла.
     */
    @Transactional
    public void importAllFromCSV(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String currentTable = null;
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("TABLE:")) {
                    currentTable = line.split(":")[1].trim();
                } else if (currentTable != null && !line.isEmpty() && !line.contains(",")) {
                    String sql = "INSERT INTO " + currentTable + " VALUES (" + line + ")";
                    jdbcTemplate.execute(sql);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении CSV-файла: " + e.getMessage(), e);
        }
    }

    /**
     * Импорт всех таблиц из SQL-файла.
     */
    @Transactional
    public void importAllFromSQL(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder sqlBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line.trim());
                if (line.endsWith(";")) {
                    jdbcTemplate.execute(sqlBuilder.toString());
                    sqlBuilder.setLength(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения SQL-файла: " + e.getMessage(), e);
        }
    }


    // Метод для создания резервной копии
    public String createBackup() {
        return exportAllToSQL();  // Вы можете изменить это на CSV, если необходимо
    }




    /**
     * Получение списка всех таблиц в базе данных.
     */
    private List<String> getAllTableNames() {
        return jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema='public'",
                String.class
        );
    }
}
