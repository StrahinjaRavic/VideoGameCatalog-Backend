package com.nsi.gamecatalog.service;

import com.nsi.gamecatalog.dto.PlatformDto;
import com.nsi.gamecatalog.entity.Platform;
import com.nsi.gamecatalog.exception.ApiException;
import com.nsi.gamecatalog.repository.PlatformRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlatformService {
    private final PlatformRepository repo;

    public PlatformService(PlatformRepository repo) { this.repo = repo; }

    public List<PlatformDto> list() {
        return repo.findAll().stream()
                .map(p -> new PlatformDto(p.getId(), p.getName()))
                .toList();
    }

    @Transactional
    public PlatformDto create(PlatformDto dto) {
        if (repo.existsByName(dto.name())) {
            throw new ApiException(HttpStatus.CONFLICT, "Platform already exists");
        }
        Platform p = repo.save(new Platform(dto.name()));
        return new PlatformDto(p.getId(), p.getName());
    }

    @Transactional
    public PlatformDto update(Long id, PlatformDto dto) {
        Platform p = repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Platform not found"));
        p.setName(dto.name());
        return new PlatformDto(p.getId(), p.getName());
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Platform not found");
        }
        try {
            repo.deleteById(id);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.CONFLICT, "Platform is in use by games");
        }
    }
}
