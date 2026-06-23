package com.nsi.gamecatalog.controller;

import com.nsi.gamecatalog.dto.CategoryDto;
import com.nsi.gamecatalog.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {
    private final CategoryService service;

    public CategoryController(CategoryService service) { this.service = service; }

    @GetMapping("/categories")
    public List<CategoryDto> list() { return service.list(); }

    @PostMapping("/admin/categories")
    public CategoryDto create(@Valid @RequestBody CategoryDto dto) { return service.create(dto); }

    @PutMapping("/admin/categories/{id}")
    public CategoryDto update(@PathVariable Long id, @Valid @RequestBody CategoryDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/admin/categories/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }
}
