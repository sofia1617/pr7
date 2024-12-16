package com.example.project2.service;

import com.example.project2.model.ChartModel;
import com.example.project2.repository.ChartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChartService {

    @Autowired
    private ChartRepository chartRepository;

    public List<ChartModel> findAll() {
        return chartRepository.findAll();
    }

    public ChartModel findById(Long id) {
        return chartRepository.findById(id).orElse(null);
    }

    public ChartModel save(ChartModel chart) {
        chartRepository.save(chart);
        return chart;
    }

    public void deleteById(Long id) {
        chartRepository.deleteById(id);
    }
    public List<ChartModel> findByNameContainingIgnoreCase(String name) {
        return chartRepository.findByNameContainingIgnoreCase(name);
    }
}
