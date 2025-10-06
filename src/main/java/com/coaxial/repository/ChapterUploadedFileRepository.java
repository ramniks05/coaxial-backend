package com.coaxial.repository;

import com.coaxial.entity.ChapterUploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterUploadedFileRepository extends JpaRepository<ChapterUploadedFile, Long> {}


