package com.travel.journal.repo;

import com.travel.journal.entity.TravelMemoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TravelMemoRepository extends JpaRepository<TravelMemoEntity, Integer> {
    List<TravelMemoEntity> findByTitleContainingIgnoreCase(String title);
    @Query("SELECT DISTINCT m FROM TravelMemoEntity m " +
            "LEFT JOIN FETCH m.locations " +
            "LEFT JOIN FETCH m.tags " +
            "WHERE m.user.email = :email")
    List<TravelMemoEntity> findByUserEmailWithAll(@Param("email") String email);
}
