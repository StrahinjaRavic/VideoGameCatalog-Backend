package com.nsi.gamecatalog.repository;

import com.nsi.gamecatalog.entity.Review;
import com.nsi.gamecatalog.entity.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByUserIdAndGameId(Long userId, Long gameId);

    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.game.id = :gameId")
    List<Review> findByGameIdWithUser(@Param("gameId") Long gameId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.game.id = :gameId")
    Double averageRatingForGame(@Param("gameId") Long gameId);

    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.game WHERE r.status = :status")
    List<Review> findByStatusWithAssociations(@Param("status") ReviewStatus status);

    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.game")
    List<Review> findAllWithAssociations();
}
