package com.example.project2.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogService {

    private static final String LOG_DIRECTORY = "logs/";

    public void logAction(String action) throws IOException {
        String logFileName = "application_log.txt";
        File logFile = new File(LOG_DIRECTORY + logFileName);

        // Создаем файл логов, если его нет
        if (!logFile.exists()) {
            logFile.getParentFile().mkdirs();
            logFile.createNewFile();
        }

        // Логируем действия
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] " + action + "\n");
        }
    }

    public List<String> getAllLogs() throws IOException {
        String logFileName = LOG_DIRECTORY + "application_log.txt";
        File logFile = new File(logFileName);

        if (!logFile.exists()) {
            return new ArrayList<>(); // Возвращаем пустой список, если файла логов нет
        }

        return Files.readAllLines(Paths.get(logFileName)); // Читаем все строки из файла
    }
}
