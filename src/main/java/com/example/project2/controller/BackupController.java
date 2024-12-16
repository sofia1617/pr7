package com.example.project2.controller;

import com.example.project2.service.BackupService;
import com.example.project2.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/adminbd")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @Autowired
    private LogService logService;

    @GetMapping("/backup")
    public String showBackupPage(Model model) {
        try {
            model.addAttribute("logs", logService.getAllLogs());
        } catch (IOException e) {
            model.addAttribute("error", "Ошибка при загрузке логов: " + e.getMessage());
        }
        return "adminbd/backup"; // Шаблон страницы резервного копирования
    }

    @PostMapping("/createBackup")
    public String createBackup(RedirectAttributes redirectAttributes) {
        try {
            String backupFileName = backupService.createBackup();
            redirectAttributes.addFlashAttribute("message", "Резервная копия успешно создана: " + backupFileName);
            logService.logAction("Создана резервная копия: " + backupFileName);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании резервной копии: " + e.getMessage());
        }
        return "redirect:/adminbd/backup";
    }
}
