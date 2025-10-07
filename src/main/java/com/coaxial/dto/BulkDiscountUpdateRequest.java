package com.coaxial.dto;

import com.coaxial.enums.SubscriptionLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Request DTO for bulk updating discounts by course type
 */
public class BulkDiscountUpdateRequest {
    
    @NotNull(message = "Course type ID is required")
    private Long courseTypeId;
    
    @NotNull(message = "Subscription level is required")
    private SubscriptionLevel level; // COURSE, CLASS, or EXAM
    
    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    private Integer monthlyDiscountPercent;
    
    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    private Integer quarterlyDiscountPercent;
    
    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    private Integer yearlyDiscountPercent;
    
    // Common offer validity dates for all pricing tiers
    private LocalDateTime offerValidFrom;
    private LocalDateTime offerValidTo;
    
    private LocalDateTime effectiveDate; // Optional: when to start applying these discounts
    
    // Constructors
    public BulkDiscountUpdateRequest() {}
    
    // Getters and Setters
    public Long getCourseTypeId() {
        return courseTypeId;
    }
    
    public void setCourseTypeId(Long courseTypeId) {
        this.courseTypeId = courseTypeId;
    }
    
    public SubscriptionLevel getLevel() {
        return level;
    }
    
    public void setLevel(SubscriptionLevel level) {
        this.level = level;
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
    
    public LocalDateTime getOfferValidFrom() {
        return offerValidFrom;
    }
    
    public void setOfferValidFrom(LocalDateTime offerValidFrom) {
        this.offerValidFrom = offerValidFrom;
    }
    
    public LocalDateTime getOfferValidTo() {
        return offerValidTo;
    }
    
    public void setOfferValidTo(LocalDateTime offerValidTo) {
        this.offerValidTo = offerValidTo;
    }
    
    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }
    
    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    @Override
    public String toString() {
        return "BulkDiscountUpdateRequest{" +
                "courseTypeId=" + courseTypeId +
                ", level=" + level +
                ", monthlyDiscountPercent=" + monthlyDiscountPercent +
                ", quarterlyDiscountPercent=" + quarterlyDiscountPercent +
                ", yearlyDiscountPercent=" + yearlyDiscountPercent +
                ", effectiveDate=" + effectiveDate +
                '}';
    }
}

