package com.example.project2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BackupService {

    // Чтение значения из конфигурации
    @Value("${backup.directory}")
    private String backupDirectory;

    public String createBackup() throws IOException {
        // Создаём имя файла резервной копии
        String backupFileName = "backup_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql";
        File backupFile = new File(backupDirectory + File.separator + backupFileName);

        // Проверяем, существует ли директория для резервных копий
        File backupDir = new File(backupDirectory);
        if (!backupDir.exists()) {
            // Если директории нет, создаём её
            boolean dirCreated = backupDir.mkdirs();
            if (!dirCreated) {
                throw new IOException("Не удалось создать директорию для резервных копий");
            }
        }

        // Создаём файл резервной копии, если его нет
        if (!backupFile.exists()) {
            backupFile.createNewFile();
        }

        // Логика для создания резервной копии базы данных
        try (FileWriter writer = new FileWriter(backupFile)) {
            writer.write("Резервная копия базы данных на: " + LocalDateTime.now());
        }

        // Возвращаем имя файла резервной копии
        return backupFileName;
    }
}
