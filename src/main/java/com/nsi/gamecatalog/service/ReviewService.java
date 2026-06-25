package com.nsi.gamecatalog.service;

import com.nsi.gamecatalog.dto.ReviewDtos.*;
import com.nsi.gamecatalog.entity.*;
import com.nsi.gamecatalog.exception.ApiException;
import com.nsi.gamecatalog.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final GameRepository gameRepo;
    private final AppUserRepository userRepo;

    public ReviewService(ReviewRepository reviewRepo, GameRepository gameRepo, AppUserRepository userRepo) {
        this.reviewRepo = reviewRepo;
        this.gameRepo = gameRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<PublicReview> listForGame(Long gameId) {
        return reviewRepo.findByGameIdWithUser(gameId).stream()
                .map(this::toPublic)
                .toList();
    }

    @Transactional
    public PublicReview upsertForUser(Long userId, Long gameId, ReviewRequest req) {
        AppUser user = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
        Game game = gameRepo.findById(gameId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Game not found"));

        Review r = reviewRepo.findByUserIdAndGameId(userId, gameId).orElse(null);
        if (r == null) {
            r = new Review();
            r.setUser(user);
            r.setGame(game);
            r.setRating(req.rating());
            r.setComment(req.comment());
            r.setStatus(ReviewStatus.PENDING);
        } else {
            r.setRating(req.rating());
            r.setComment(req.comment());
            r.setStatus(ReviewStatus.PENDING);
            r.setRejectionReason(null);
        }
        r = reviewRepo.save(r);
        return toPublic(r);
    }

    @Transactional
    public void deleteForUser(Long userId, Long reviewId) {
        Review r = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Review not found"));
        if (!r.getUser().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your review");
        }
        reviewRepo.delete(r);
    }

    @Transactional(readOnly = true)
    public List<AdminReview> adminList(ReviewStatus status) {
        List<Review> list = status == null
                ? reviewRepo.findAllWithAssociations()
                : reviewRepo.findByStatusWithAssociations(status);
        return list.stream().map(this::toAdmin).toList();
    }

    @Transactional
    public AdminReview approve(Long reviewId) {
        Review r = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Review not found"));
        r.setStatus(ReviewStatus.APPROVED);
        r.setRejectionReason(null);
        return toAdmin(r);
    }

    @Transactional
    public AdminReview reject(Long reviewId, RejectRequest req) {
        Review r = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Review not found"));
        r.setStatus(ReviewStatus.REJECTED);
        r.setRejectionReason(req.reason());
        return toAdmin(r);
    }

    @Transactional
    public AdminReview editComment(Long reviewId, EditCommentRequest req) {
        Review r = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Review not found"));
        r.setComment(req.comment());
        return toAdmin(r);
    }

    private PublicReview toPublic(Review r) {
        String fullName = r.getUser().getFirstName() + " " + r.getUser().getLastName();
        return new PublicReview(
                r.getId(), r.getRating(), r.getComment(), r.getRejectionReason(),
                r.getStatus(), fullName, r.getUser().getCity(),
                r.getCreatedAt(), r.getUpdatedAt()
        );
    }

    private AdminReview toAdmin(Review r) {
        String fullName = r.getUser().getFirstName() + " " + r.getUser().getLastName();
        return new AdminReview(
                r.getId(), r.getGame().getId(), r.getGame().getTitle(),
                r.getUser().getId(), fullName, r.getRating(), r.getComment(),
                r.getStatus(), r.getRejectionReason(), r.getCreatedAt(), r.getUpdatedAt()
        );
    }
}
