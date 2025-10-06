package com.coaxial.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coaxial.dto.TestQuestionRequestDTO;
import com.coaxial.dto.TestQuestionSummaryDTO;
import com.coaxial.dto.TestRequestDTO;
import com.coaxial.dto.TestResponseDTO;
import com.coaxial.service.TestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/master-data/tests")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private TestService testService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestResponseDTO> createTest(@Valid @RequestBody TestRequestDTO request) {
        TestResponseDTO created = testService.createTest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<List<TestResponseDTO>> getAllTests() {
        return ResponseEntity.ok(testService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<TestResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getByIdAsDTO(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestResponseDTO> update(@PathVariable Long id, @Valid @RequestBody TestRequestDTO request) {
        return ResponseEntity.ok(testService.updateTest(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        testService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TestQuestionSummaryDTO>> getQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getQuestions(id));
    }

    @PostMapping("/{id}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestQuestionSummaryDTO> addQuestion(
            @PathVariable Long id,
            @Valid @RequestBody TestQuestionRequestDTO question) {
        TestQuestionSummaryDTO added = testService.addQuestion(id, question);
        return ResponseEntity.status(HttpStatus.CREATED).body(added);
    }

    @PutMapping("/{id}/questions/{qId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestQuestionSummaryDTO> updateQuestion(
            @PathVariable Long id,
            @PathVariable Long qId,
            @Valid @RequestBody TestQuestionRequestDTO question) {
        return ResponseEntity.ok(testService.updateQuestion(id, qId, question));
    }

    @DeleteMapping("/{id}/questions/{qId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id,
            @PathVariable Long qId) {
        testService.removeQuestion(id, qId);
        return ResponseEntity.noContent().build();
    }
}


