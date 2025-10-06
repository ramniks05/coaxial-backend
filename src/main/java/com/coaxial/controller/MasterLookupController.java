package com.coaxial.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coaxial.entity.MasterExam;
import com.coaxial.entity.MasterYear;
import com.coaxial.repository.MasterExamRepository;
import com.coaxial.repository.MasterYearRepository;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class MasterLookupController {

    @Autowired
    private MasterYearRepository masterYearRepository;

    @Autowired
    private MasterExamRepository masterExamRepository;

    @PreAuthorize("permitAll()")
    @GetMapping("/years")
    public ResponseEntity<List<MasterYear>> getYears() {
        List<MasterYear> years = masterYearRepository.findAllByOrderByYearValueDesc();
        return ResponseEntity.ok(years);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/master-exams")
    public ResponseEntity<List<MasterExam>> getMasterExams() {
        List<MasterExam> exams = masterExamRepository.findAll();
        return ResponseEntity.ok(exams);
    }

    // Simplified key/value lists
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    @GetMapping("/master-exams/kv")
    public ResponseEntity<List<Map<String, Object>>> getMasterExamsKeyValue() {
        List<Map<String, Object>> data = masterExamRepository.findAll().stream()
                .map(e -> {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("id", e.getId());
                    m.put("exam", e.getExamName());
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(data);
    }

    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    @GetMapping("/years/kv")
    public ResponseEntity<List<Map<String, Object>>> getYearsKeyValue() {
        List<Map<String, Object>> data = masterYearRepository.findAllByOrderByYearValueDesc().stream()
                .map(y -> {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("id", y.getId());
                    m.put("year", y.getYearValue());
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(data);
    }
}


