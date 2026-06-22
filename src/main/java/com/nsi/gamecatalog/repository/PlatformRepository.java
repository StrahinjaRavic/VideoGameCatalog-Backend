package com.nsi.gamecatalog.repository;

import com.nsi.gamecatalog.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformRepository extends JpaRepository<Platform, Long> {
    boolean existsByName(String name);
}
