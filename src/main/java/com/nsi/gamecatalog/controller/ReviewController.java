package com.nsi.gamecatalog.controller;

import com.nsi.gamecatalog.dto.ReviewDtos.*;
import com.nsi.gamecatalog.entity.ReviewStatus;
import com.nsi.gamecatalog.exception.ApiException;
import com.nsi.gamecatalog.security.UserPrincipal;
import com.nsi.gamecatalog.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) { this.service = service; }

    @GetMapping("/reviews/game/{gameId}")
    public List<PublicReview> listForGame(@PathVariable Long gameId) {
        return service.listForGame(gameId);
    }

    @PostMapping("/reviews/game/{gameId}")
    public PublicReview upsert(@PathVariable Long gameId,
                               @Valid @RequestBody ReviewRequest req,
                               @AuthenticationPrincipal UserPrincipal principal) {
        requireUser(principal);
        return service.upsertForUser(principal.id(), gameId, req);
    }

    @DeleteMapping("/reviews/{id}")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        requireUser(principal);
        service.deleteForUser(principal.id(), id);
    }

    @GetMapping("/admin/reviews")
    public List<AdminReview> adminList(@RequestParam(required = false) ReviewStatus status) {
        return service.adminList(status);
    }

    @PostMapping("/admin/reviews/{id}/approve")
    public AdminReview approve(@PathVariable Long id) { return service.approve(id); }

    @PostMapping("/admin/reviews/{id}/reject")
    public AdminReview reject(@PathVariable Long id, @Valid @RequestBody RejectRequest req) {
        return service.reject(id, req);
    }

    @PutMapping("/admin/reviews/{id}/comment")
    public AdminReview editComment(@PathVariable Long id, @Valid @RequestBody EditCommentRequest req) {
        return service.editComment(id, req);
    }

    private void requireUser(UserPrincipal principal) {
        if (principal == null || !"USER".equals(principal.role())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "User role required");
        }
    }
}
