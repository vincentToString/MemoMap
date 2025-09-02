package com.travel.journal.repo;

import com.travel.journal.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<LocationEntity, Integer> {
    Optional<LocationEntity> findByPlaceNameIgnoreCase(String placeName);

}
