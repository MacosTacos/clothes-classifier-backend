package org.example.clothesclassifier.repositories;

import org.example.clothesclassifier.entities.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {

    boolean existsByAuthority(String authority);

    List<AuthorityEntity> findByAuthorityContainingIgnoreCase(String authority);
}
