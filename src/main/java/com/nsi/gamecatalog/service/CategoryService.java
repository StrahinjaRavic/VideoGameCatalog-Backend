package com.nsi.gamecatalog.service;

import com.nsi.gamecatalog.dto.CategoryDto;
import com.nsi.gamecatalog.entity.Category;
import com.nsi.gamecatalog.exception.ApiException;
import com.nsi.gamecatalog.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) { this.repo = repo; }

    public List<CategoryDto> list() {
        return repo.findAll().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName()))
                .toList();
    }

    @Transactional
    public CategoryDto create(CategoryDto dto) {
        if (repo.existsByName(dto.name())) {
            throw new ApiException(HttpStatus.CONFLICT, "Category already exists");
        }
        Category c = repo.save(new Category(dto.name()));
        return new CategoryDto(c.getId(), c.getName());
    }

    @Transactional
    public CategoryDto update(Long id, CategoryDto dto) {
        Category c = repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));
        c.setName(dto.name());
        return new CategoryDto(c.getId(), c.getName());
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Category not found");
        }
        try {
            repo.deleteById(id);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.CONFLICT, "Category is in use by games");
        }
    }
}
