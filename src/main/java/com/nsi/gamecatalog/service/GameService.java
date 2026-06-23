package com.nsi.gamecatalog.service;

import com.nsi.gamecatalog.dto.GameDtos.*;
import com.nsi.gamecatalog.dto.PlatformDto;
import com.nsi.gamecatalog.entity.*;
import com.nsi.gamecatalog.exception.ApiException;
import com.nsi.gamecatalog.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final GameRepository gameRepo;
    private final CategoryRepository categoryRepo;
    private final PlatformRepository platformRepo;
    private final ReviewRepository reviewRepo;

    public GameService(GameRepository gameRepo, CategoryRepository categoryRepo,
                       PlatformRepository platformRepo, ReviewRepository reviewRepo) {
        this.gameRepo = gameRepo;
        this.categoryRepo = categoryRepo;
        this.platformRepo = platformRepo;
        this.reviewRepo = reviewRepo;
    }

    @Transactional(readOnly = true)
    public List<GameSummary> list() {
        return gameRepo.findAllWithAssociations().stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public GameDetail get(Long id) {
        Game g = gameRepo.findByIdWithAssociations(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Game not found"));
        return toDetail(g);
    }

    @Transactional(readOnly = true)
    public byte[] getImage(Long id) {
        Game g = gameRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Game not found"));
        if (g.getImage() == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No image");
        }
        return g.getImage();
    }

    @Transactional(readOnly = true)
    public String getImageContentType(Long id) {
        return gameRepo.findById(id).map(Game::getImageContentType).orElse("application/octet-stream");
    }

    @Transactional
    public GameDetail create(GameRequest req) {
        Game g = new Game();
        applyRequest(g, req);
        Game saved = gameRepo.save(g);
        return toDetail(saved);
    }

    @Transactional
    public GameDetail update(Long id, GameRequest req) {
        Game g = gameRepo.findByIdWithAssociations(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Game not found"));
        applyRequest(g, req);
        return toDetail(g);
    }

    @Transactional
    public void uploadImage(Long id, MultipartFile file) {
        Game g = gameRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Game not found"));
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Empty file");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "File must be an image");
        }
        try {
            g.setImage(file.getBytes());
            g.setImageContentType(contentType);
        } catch (IOException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Failed to read image");
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!gameRepo.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Game not found");
        }
        gameRepo.deleteById(id);
    }

    private void applyRequest(Game g, GameRequest req) {
        Category cat = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid category"));
        Set<Platform> platforms = new HashSet<>(platformRepo.findAllById(req.platformIds()));
        if (platforms.size() != req.platformIds().size()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid platform ids");
        }
        g.setTitle(req.title());
        g.setPublisher(req.publisher());
        g.setReleaseYear(req.releaseYear());
        g.setDescription(req.description());
        g.setEsrbRating(req.esrbRating());
        g.setForSale(req.forSale());
        g.setPrice(req.forSale() ? req.price() : null);
        g.setCategory(cat);
        g.getPlatforms().clear();
        g.getPlatforms().addAll(platforms);

        g.getEsrbFacts().clear();
        if (req.esrbFacts() != null) {
            for (String text : req.esrbFacts()) {
                g.getEsrbFacts().add(new EsrbFact(g, text));
            }
        }
    }

    private GameSummary toSummary(Game g) {
        Double avg = reviewRepo.averageRatingForGame(g.getId());
        return new GameSummary(
                g.getId(), g.getTitle(), g.getPublisher(), g.getReleaseYear(),
                g.getEsrbRating(), g.getForSale(), g.getPrice(),
                g.getCategory().getId(), g.getCategory().getName(),
                avg, g.getImage() != null
        );
    }

    private GameDetail toDetail(Game g) {
        Double avg = reviewRepo.averageRatingForGame(g.getId());
        List<PlatformDto> platformDtos = g.getPlatforms().stream()
                .sorted(Comparator.comparing(Platform::getName))
                .map(p -> new PlatformDto(p.getId(), p.getName()))
                .collect(Collectors.toList());
        List<String> facts = g.getEsrbFacts().stream().map(EsrbFact::getText).toList();
        return new GameDetail(
                g.getId(), g.getTitle(), g.getPublisher(), g.getReleaseYear(),
                g.getDescription(), g.getEsrbRating(), g.getForSale(), g.getPrice(),
                g.getCategory().getId(), g.getCategory().getName(),
                platformDtos, facts, avg, g.getImage() != null
        );
    }
}
