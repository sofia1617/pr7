package com.example.project2.service;

import com.example.project2.model.RecordsModel;
import com.example.project2.repository.RecordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class RecordsService {

    private final RecordsRepository recordsRepository;

    @Autowired
    public RecordsService(RecordsRepository recordsRepository) {
        this.recordsRepository = recordsRepository;
    }

    // Метод для получения всех товаров
    public List<RecordsModel> findAll() {
        return recordsRepository.findAll();
    }

    // Метод для получения всех товаров с пагинацией
    public Page<RecordsModel> findPaginated(Pageable pageable) {
        return recordsRepository.findAll(pageable);
    }

    // Метод для поиска товаров по имени с пагинацией
    public Page<RecordsModel> findByNameContainingIgnoreCase(String keyword, Pageable pageable) {
        return recordsRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    public Optional<RecordsModel> findById(Long id) {
        return recordsRepository.findById(id);
    }

    public RecordsModel save(RecordsModel recordsModel) {
        return recordsRepository.save(recordsModel);
    }

    public void deleteById(Long id) {
        recordsRepository.deleteById(id);
    }

    public Page<RecordsModel> findAll(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10);
        if (search != null && !search.isEmpty()) {
            return recordsRepository.findByNameContainingIgnoreCase(search, pageable);
        }
        return recordsRepository.findAll(pageable);
    }

}
