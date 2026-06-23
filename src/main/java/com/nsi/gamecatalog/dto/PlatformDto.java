package com.nsi.gamecatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlatformDto(
        Long id,
        @NotBlank @Size(max = 100) String name
) {}
