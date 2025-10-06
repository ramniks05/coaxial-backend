package com.coaxial.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Topic name is required")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Course type determines which relationship table to use
    @NotNull(message = "Course type ID is required")
    @Column(name = "course_type_id", nullable = false)
    private Long courseTypeId;

    // Relationship ID (classSubjectId, examSubjectId, or courseSubjectId)
    @NotNull(message = "Relationship ID is required")
    @Column(name = "relationship_id", nullable = false)
    private Long relationshipId;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    // Constructors
    public Topic() {}

    public Topic(String name, String description, Long courseTypeId, Long relationshipId) {
        this.name = name;
        this.description = description;
        this.courseTypeId = courseTypeId;
        this.relationshipId = relationshipId;
    }

    public Topic(String name, String description, Long courseTypeId, Long relationshipId, Integer displayOrder) {
        this.name = name;
        this.description = description;
        this.courseTypeId = courseTypeId;
        this.relationshipId = relationshipId;
        this.displayOrder = displayOrder;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCourseTypeId() { return courseTypeId; }
    public void setCourseTypeId(Long courseTypeId) { this.courseTypeId = courseTypeId; }

    public Long getRelationshipId() { return relationshipId; }
    public void setRelationshipId(Long relationshipId) { this.relationshipId = relationshipId; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public User getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }

    // Helper methods for context information
    public String getContextType() {
        if (courseTypeId == 1) return "ACADEMIC";
        if (courseTypeId == 2) return "COMPETITIVE";
        if (courseTypeId == 3) return "PROFESSIONAL";
        return "UNKNOWN";
    }

    public String getCourseTypeName() {
        if (courseTypeId == 1) return "Academic";
        if (courseTypeId == 2) return "Competitive";
        if (courseTypeId == 3) return "Professional";
        return "Unknown";
    }

    // Helper method to get relationship type
    public String getRelationshipType() {
        if (courseTypeId == 1) return "ClassSubject";
        if (courseTypeId == 2) return "ExamSubject";
        if (courseTypeId == 3) return "CourseSubject";
        return "Unknown";
    }
}