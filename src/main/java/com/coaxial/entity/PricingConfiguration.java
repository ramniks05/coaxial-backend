package com.coaxial.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity for storing admin-defined pricing configurations
 * Supports pricing at Course, Class, and Exam levels
 */
@Entity
@Table(name = "pricing_configurations")
@EntityListeners(AuditingEntityListener.class)
public class PricingConfiguration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Entity type is required")
    @Column(name = "entity_type", nullable = false)
    private String entityType; // COURSE, CLASS, EXAM
    
    @NotNull(message = "Entity ID is required")
    @Column(name = "entity_id", nullable = false)
    private Long entityId; // courseId, classId, or examId
    
    @NotBlank(message = "Entity name is required")
    @Column(name = "entity_name")
    private String entityName; // for display purposes
    
    @Column(name = "course_type_id")
    private Long courseTypeId; // For bulk discount updates by course type
    
    // Pricing (in rupees for precision)
    @NotNull(message = "Monthly price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(name = "monthly_price", precision = 10, scale = 2)
    private BigDecimal monthlyPrice;
    
    @NotNull(message = "Quarterly price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(name = "quarterly_price", precision = 10, scale = 2)
    private BigDecimal quarterlyPrice;
    
    @NotNull(message = "Yearly price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(name = "yearly_price", precision = 10, scale = 2)
    private BigDecimal yearlyPrice;
    
    // Discount percentages
    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    @Column(name = "monthly_discount_percent")
    private Integer monthlyDiscountPercent = 0;
    
    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    @Column(name = "quarterly_discount_percent")
    private Integer quarterlyDiscountPercent = 10;
    
    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    @Column(name = "yearly_discount_percent")
    private Integer yearlyDiscountPercent = 20;
    
    // Offer validity dates for Monthly
    @Column(name = "monthly_offer_valid_from")
    private LocalDateTime monthlyOfferValidFrom;
    
    @Column(name = "monthly_offer_valid_to")
    private LocalDateTime monthlyOfferValidTo;
    
    // Offer validity dates for Quarterly
    @Column(name = "quarterly_offer_valid_from")
    private LocalDateTime quarterlyOfferValidFrom;
    
    @Column(name = "quarterly_offer_valid_to")
    private LocalDateTime quarterlyOfferValidTo;
    
    // Offer validity dates for Yearly
    @Column(name = "yearly_offer_valid_from")
    private LocalDateTime yearlyOfferValidFrom;
    
    @Column(name = "yearly_offer_valid_to")
    private LocalDateTime yearlyOfferValidTo;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    
    // Constructors
    public PricingConfiguration() {}
    
    public PricingConfiguration(String entityType, Long entityId, String entityName, 
                              BigDecimal monthlyPrice, BigDecimal quarterlyPrice, BigDecimal yearlyPrice) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityName = entityName;
        this.monthlyPrice = monthlyPrice;
        this.quarterlyPrice = quarterlyPrice;
        this.yearlyPrice = yearlyPrice;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public Long getEntityId() {
        return entityId;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    
    public String getEntityName() {
        return entityName;
    }
    
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
    
    public Long getCourseTypeId() {
        return courseTypeId;
    }
    
    public void setCourseTypeId(Long courseTypeId) {
        this.courseTypeId = courseTypeId;
    }
    
    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }
    
    public void setMonthlyPrice(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }
    
    public BigDecimal getQuarterlyPrice() {
        return quarterlyPrice;
    }
    
    public void setQuarterlyPrice(BigDecimal quarterlyPrice) {
        this.quarterlyPrice = quarterlyPrice;
    }
    
    public BigDecimal getYearlyPrice() {
        return yearlyPrice;
    }
    
    public void setYearlyPrice(BigDecimal yearlyPrice) {
        this.yearlyPrice = yearlyPrice;
    }
    
    public Integer getMonthlyDiscountPercent() {
        return monthlyDiscountPercent;
    }
    
    public void setMonthlyDiscountPercent(Integer monthlyDiscountPercent) {
        this.monthlyDiscountPercent = monthlyDiscountPercent;
    }
    
    public Integer getQuarterlyDiscountPercent() {
        return quarterlyDiscountPercent;
    }
    
    public void setQuarterlyDiscountPercent(Integer quarterlyDiscountPercent) {
        this.quarterlyDiscountPercent = quarterlyDiscountPercent;
    }
    
    public Integer getYearlyDiscountPercent() {
        return yearlyDiscountPercent;
    }
    
    public void setYearlyDiscountPercent(Integer yearlyDiscountPercent) {
        this.yearlyDiscountPercent = yearlyDiscountPercent;
    }
    
    public LocalDateTime getMonthlyOfferValidFrom() {
        return monthlyOfferValidFrom;
    }
    
    public void setMonthlyOfferValidFrom(LocalDateTime monthlyOfferValidFrom) {
        this.monthlyOfferValidFrom = monthlyOfferValidFrom;
    }
    
    public LocalDateTime getMonthlyOfferValidTo() {
        return monthlyOfferValidTo;
    }
    
    public void setMonthlyOfferValidTo(LocalDateTime monthlyOfferValidTo) {
        this.monthlyOfferValidTo = monthlyOfferValidTo;
    }
    
    public LocalDateTime getQuarterlyOfferValidFrom() {
        return quarterlyOfferValidFrom;
    }
    
    public void setQuarterlyOfferValidFrom(LocalDateTime quarterlyOfferValidFrom) {
        this.quarterlyOfferValidFrom = quarterlyOfferValidFrom;
    }
    
    public LocalDateTime getQuarterlyOfferValidTo() {
        return quarterlyOfferValidTo;
    }
    
    public void setQuarterlyOfferValidTo(LocalDateTime quarterlyOfferValidTo) {
        this.quarterlyOfferValidTo = quarterlyOfferValidTo;
    }
    
    public LocalDateTime getYearlyOfferValidFrom() {
        return yearlyOfferValidFrom;
    }
    
    public void setYearlyOfferValidFrom(LocalDateTime yearlyOfferValidFrom) {
        this.yearlyOfferValidFrom = yearlyOfferValidFrom;
    }
    
    public LocalDateTime getYearlyOfferValidTo() {
        return yearlyOfferValidTo;
    }
    
    public void setYearlyOfferValidTo(LocalDateTime yearlyOfferValidTo) {
        this.yearlyOfferValidTo = yearlyOfferValidTo;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public User getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    @Override
    public String toString() {
        return "PricingConfiguration{" +
                "id=" + id +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", entityName='" + entityName + '\'' +
                ", monthlyPrice=" + monthlyPrice +
                ", quarterlyPrice=" + quarterlyPrice +
                ", yearlyPrice=" + yearlyPrice +
                ", monthlyDiscountPercent=" + monthlyDiscountPercent +
                ", quarterlyDiscountPercent=" + quarterlyDiscountPercent +
                ", yearlyDiscountPercent=" + yearlyDiscountPercent +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
