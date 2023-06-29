package com.brandyodhiambo.JWTAuthentication.repository;


import com.brandyodhiambo.JWTAuthentication.model.Notes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotesRepository extends JpaRepository<Notes,Long> {

    Page<Notes> findAll(Pageable pageable);
    Page<Notes> findByPublished(boolean published, Pageable pageable);
    Page<Notes> findByTitleContaining(String title, Pageable pageable);
}
