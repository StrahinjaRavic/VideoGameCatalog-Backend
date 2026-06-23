package com.nsi.gamecatalog.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class GameDtos {

    public record GameSummary(
            Long id,
            String title,
            String publisher,
            Integer releaseYear,
            String esrbRating,
            Boolean forSale,
            BigDecimal price,
            Long categoryId,
            String categoryName,
            Double averageRating,
            Boolean hasImage
    ) {}

    public record GameDetail(
            Long id,
            String title,
            String publisher,
            Integer releaseYear,
            String description,
            String esrbRating,
            Boolean forSale,
            BigDecimal price,
            Long categoryId,
            String categoryName,
            List<PlatformDto> platforms,
            List<String> esrbFacts,
            Double averageRating,
            Boolean hasImage
    ) {}

    public record GameRequest(
            @NotBlank @Size(max = 200) String title,
            @NotBlank @Size(max = 150) String publisher,
            @NotNull @Min(1970) @Max(2100) Integer releaseYear,
            @Size(max = 10000) String description,
            @NotBlank @Size(max = 20) String esrbRating,
            @NotNull Boolean forSale,
            @DecimalMin(value = "0.0", inclusive = true) BigDecimal price,
            @NotNull Long categoryId,
            @NotNull Set<Long> platformIds,
            List<@NotBlank @Size(max = 500) String> esrbFacts
    ) {}
}
