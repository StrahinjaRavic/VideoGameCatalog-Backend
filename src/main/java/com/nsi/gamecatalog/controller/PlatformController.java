package com.nsi.gamecatalog.controller;

import com.nsi.gamecatalog.dto.PlatformDto;
import com.nsi.gamecatalog.service.PlatformService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlatformController {
    private final PlatformService service;

    public PlatformController(PlatformService service) { this.service = service; }

    @GetMapping("/platforms")
    public List<PlatformDto> list() { return service.list(); }

    @PostMapping("/admin/platforms")
    public PlatformDto create(@Valid @RequestBody PlatformDto dto) { return service.create(dto); }

    @PutMapping("/admin/platforms/{id}")
    public PlatformDto update(@PathVariable Long id, @Valid @RequestBody PlatformDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/admin/platforms/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }
}
