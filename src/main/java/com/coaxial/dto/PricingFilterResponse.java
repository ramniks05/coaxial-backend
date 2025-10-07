package com.coaxial.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for pricing configuration filtering
 */
public class PricingFilterResponse {
    
    private Long id;
    private String entityType; // COURSE, CLASS, EXAM
    private Long entityId;
    private String entityName;
    private Long courseTypeId;
    private String courseTypeName;
    
    // Pricing
    private BigDecimal monthlyPrice;
    private BigDecimal quarterlyPrice;
    private BigDecimal yearlyPrice;
    
    // Discounts
    private Integer monthlyDiscountPercent;
    private Integer quarterlyDiscountPercent;
    private Integer yearlyDiscountPercent;
    
    // Calculated final prices (after discount)
    private BigDecimal monthlyFinalPrice;
    private BigDecimal quarterlyFinalPrice;
    private BigDecimal yearlyFinalPrice;
    
    // Offer validity
    private LocalDateTime monthlyOfferValidFrom;
    private LocalDateTime monthlyOfferValidTo;
    private LocalDateTime quarterlyOfferValidFrom;
    private LocalDateTime quarterlyOfferValidTo;
    private LocalDateTime yearlyOfferValidFrom;
    private LocalDateTime yearlyOfferValidTo;
    
    // Status
    private Boolean isActive;
    private Boolean isOfferActive; // Whether offer is currently valid
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public PricingFilterResponse() {}
    
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
    
    public String getCourseTypeName() {
        return courseTypeName;
    }
    
    public void setCourseTypeName(String courseTypeName) {
        this.courseTypeName = courseTypeName;
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
    
    public BigDecimal getMonthlyFinalPrice() {
        return monthlyFinalPrice;
    }
    
    public void setMonthlyFinalPrice(BigDecimal monthlyFinalPrice) {
        this.monthlyFinalPrice = monthlyFinalPrice;
    }
    
    public BigDecimal getQuarterlyFinalPrice() {
        return quarterlyFinalPrice;
    }
    
    public void setQuarterlyFinalPrice(BigDecimal quarterlyFinalPrice) {
        this.quarterlyFinalPrice = quarterlyFinalPrice;
    }
    
    public BigDecimal getYearlyFinalPrice() {
        return yearlyFinalPrice;
    }
    
    public void setYearlyFinalPrice(BigDecimal yearlyFinalPrice) {
        this.yearlyFinalPrice = yearlyFinalPrice;
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
    
    public Boolean getIsOfferActive() {
        return isOfferActive;
    }
    
    public void setIsOfferActive(Boolean isOfferActive) {
        this.isOfferActive = isOfferActive;
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
}

