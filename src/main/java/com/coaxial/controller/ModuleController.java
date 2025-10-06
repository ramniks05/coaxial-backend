package com.coaxial.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coaxial.dto.ModuleRequestDTO;
import com.coaxial.dto.ModuleResponseDTO;
import com.coaxial.service.ModuleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/master-data/modules")
@PreAuthorize("hasRole('ADMIN')")
public class ModuleController {
    
    @Autowired
    private ModuleService moduleService;
    
    // Get all modules
    @GetMapping
    public ResponseEntity<List<ModuleResponseDTO>> getAllModules() {
        List<ModuleResponseDTO> modules = moduleService.getAllModules();
        return ResponseEntity.ok(modules);
    }
    
    // Get modules by topic ID
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<ModuleResponseDTO>> getModulesByTopicId(@PathVariable Long topicId) {
        List<ModuleResponseDTO> modules = moduleService.getModulesByTopicId(topicId);
        return ResponseEntity.ok(modules);
    }
    
    // Get modules with filters and pagination
    @GetMapping("/filtered")
    public ResponseEntity<Page<ModuleResponseDTO>> getModulesWithFilters(
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "displayOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ModuleResponseDTO> modules = moduleService.getModulesWithFilters(topicId, isActive, search, pageable);
        return ResponseEntity.ok(modules);
    }
    
    // Standardized combined filter endpoint with pagination
    @GetMapping("/combined-filter")
    public ResponseEntity<Page<ModuleResponseDTO>> getModulesCombinedFilter(
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ModuleResponseDTO> modules = moduleService.getModulesCombinedFilter(
            courseTypeId, courseId, classId, examId, subjectId, topicId, active, search, pageable);
        return ResponseEntity.ok(modules);
    }
    
    
    // Get module by ID
    @GetMapping("/{id}")
    public ResponseEntity<ModuleResponseDTO> getModuleById(@PathVariable Long id) {
        Optional<ModuleResponseDTO> module = moduleService.getModuleById(id);
        return module.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    // Create new module
    @PostMapping
    public ResponseEntity<ModuleResponseDTO> createModule(@Valid @RequestBody ModuleRequestDTO moduleRequestDTO) {
        try {
            ModuleResponseDTO createdModule = moduleService.createModule(moduleRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdModule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Update module
    @PutMapping("/{id}")
    public ResponseEntity<ModuleResponseDTO> updateModule(@PathVariable Long id, @Valid @RequestBody ModuleRequestDTO moduleRequestDTO) {
        try {
            ModuleResponseDTO updatedModule = moduleService.updateModule(id, moduleRequestDTO);
            return ResponseEntity.ok(updatedModule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Delete module
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteModule(@PathVariable Long id) {
        try {
            moduleService.deleteModule(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Module deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
}
