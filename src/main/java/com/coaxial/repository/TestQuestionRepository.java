package com.coaxial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.TestQuestion;

@Repository
public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {
    List<TestQuestion> findByTestIdOrderByQuestionOrderAsc(Long testId);

    Optional<TestQuestion> findByIdAndTestId(Long id, Long testId);

    long deleteByIdAndTestId(Long id, Long testId);
}


