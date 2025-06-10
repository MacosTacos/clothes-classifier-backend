package org.example.clothesclassifier.repositories;

import org.example.clothesclassifier.entities.ClothingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothingRepository extends JpaRepository<ClothingEntity, Long> {
    List<ClothingEntity> findByUser_Id(Long userId);
}