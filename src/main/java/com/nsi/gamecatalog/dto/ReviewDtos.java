package com.nsi.gamecatalog.dto;

import com.nsi.gamecatalog.entity.ReviewStatus;
import jakarta.validation.constraints.*;

import java.time.Instant;

public class ReviewDtos {

    public record PublicReview(
            Long id,
            Integer rating,
            String comment,
            String rejectionReason,
            ReviewStatus status,
            String userFullName,
            String userCity,
            Instant createdAt,
            Instant updatedAt
    ) {}

    public record AdminReview(
            Long id,
            Long gameId,
            String gameTitle,
            Long userId,
            String userFullName,
            Integer rating,
            String comment,
            ReviewStatus status,
            String rejectionReason,
            Instant createdAt,
            Instant updatedAt
    ) {}

    public record ReviewRequest(
            @NotNull @Min(1) @Max(5) Integer rating,
            @Size(max = 5000) String comment
    ) {}

    public record RejectRequest(
            @NotBlank @Size(max = 1000) String reason
    ) {}

    public record EditCommentRequest(
            @Size(max = 5000) String comment
    ) {}
}
