package com.nsi.gamecatalog.repository;

import com.nsi.gamecatalog.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("SELECT DISTINCT g FROM Game g LEFT JOIN FETCH g.category LEFT JOIN FETCH g.platforms")
    List<Game> findAllWithAssociations();

    @Query("SELECT g FROM Game g LEFT JOIN FETCH g.category LEFT JOIN FETCH g.platforms LEFT JOIN FETCH g.esrbFacts WHERE g.id = :id")
    Optional<Game> findByIdWithAssociations(@Param("id") Long id);
}
