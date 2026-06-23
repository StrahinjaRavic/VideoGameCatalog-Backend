package com.nsi.gamecatalog.controller;

import com.nsi.gamecatalog.dto.GameDtos.*;
import com.nsi.gamecatalog.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameController {

    private final GameService service;

    public GameController(GameService service) { this.service = service; }

    @GetMapping("/games")
    public List<GameSummary> list() { return service.list(); }

    @GetMapping("/games/{id}")
    public GameDetail get(@PathVariable Long id) { return service.get(id); }

    @GetMapping("/games/{id}/image")
    public ResponseEntity<byte[]> image(@PathVariable Long id) {
        byte[] data = service.getImage(id);
        String ct = service.getImageContentType(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(ct == null ? "application/octet-stream" : ct))
                .body(data);
    }

    @PostMapping("/admin/games")
    public GameDetail create(@Valid @RequestBody GameRequest req) { return service.create(req); }

    @PutMapping("/admin/games/{id}")
    public GameDetail update(@PathVariable Long id, @Valid @RequestBody GameRequest req) {
        return service.update(id, req);
    }

    @PostMapping(value = "/admin/games/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        service.uploadImage(id, file);
    }

    @DeleteMapping("/admin/games/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }
}
