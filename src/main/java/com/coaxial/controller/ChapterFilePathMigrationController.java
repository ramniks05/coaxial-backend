package com.coaxial.controller;

import com.coaxial.service.ChapterFilePathMigrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for migrating chapter file paths from absolute to relative format
 * This fixes the PDF loading issue in student course content
 */
@RestController
@RequestMapping("/api/admin/migration")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Data Migration", description = "APIs for data migration and fixes")
public class ChapterFilePathMigrationController {

    private final ChapterFilePathMigrationService migrationService;

    public ChapterFilePathMigrationController(ChapterFilePathMigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @Operation(
        summary = "Migrate all chapter file paths",
        description = "Converts all absolute file paths to relative paths for proper PDF loading. " +
                      "This should be run after deploying the fix for the PDF loading issue."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Migration completed successfully"),
        @ApiResponse(responseCode = "500", description = "Migration failed")
    })
    @PostMapping("/chapter-file-paths")
    public ResponseEntity<?> migrateAllFilePaths() {
        try {
            int updatedCount = migrationService.migrateAllFilePaths();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Chapter file path migration completed successfully");
            response.put("updatedCount", updatedCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Migration failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @Operation(
        summary = "Migrate single file path",
        description = "Converts a single file's absolute path to relative path for testing purposes"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File path migrated successfully"),
        @ApiResponse(responseCode = "404", description = "File not found"),
        @ApiResponse(responseCode = "500", description = "Migration failed")
    })
    @PostMapping("/chapter-file-paths/{fileId}")
    public ResponseEntity<?> migrateSingleFilePath(@PathVariable Long fileId) {
        try {
            boolean success = migrationService.migrateFilePath(fileId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? 
                "File path migrated successfully" : 
                "File not found or already in correct format");
            response.put("fileId", fileId);
            
            return success ? 
                ResponseEntity.ok(response) : 
                ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Migration failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}

