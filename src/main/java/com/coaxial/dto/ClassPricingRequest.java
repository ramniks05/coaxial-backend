package com.coaxial.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for setting class pricing in admin pricing management
 */
public class ClassPricingRequest {
    
    @NotNull(message = "Class ID is required")
    private Long classId;
    
    @NotBlank(message = "Class name is required")
    private String className;
    
    @NotNull(message = "Course ID is required")
    private Long courseId;
    
    @NotBlank(message = "Course name is required")
    private String courseName;
    
    private Long courseTypeId; // For bulk discount updates by course type
    
    @NotNull(message = "Monthly price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal monthlyPrice;
    
    @NotNull(message = "Quarterly price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal quarterlyPrice;
    
    @NotNull(message = "Yearly price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal yearlyPrice;
    
    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    private Integer monthlyDiscountPercent = 0;
    
    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    private Integer quarterlyDiscountPercent = 10;
    
    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    private Integer yearlyDiscountPercent = 20;
    
    // Offer validity dates for Monthly
    private LocalDateTime monthlyOfferValidFrom;
    private LocalDateTime monthlyOfferValidTo;
    
    // Offer validity dates for Quarterly
    private LocalDateTime quarterlyOfferValidFrom;
    private LocalDateTime quarterlyOfferValidTo;
    
    // Offer validity dates for Yearly
    private LocalDateTime yearlyOfferValidFrom;
    private LocalDateTime yearlyOfferValidTo;
    
    private Boolean isActive = true;
    
    // Constructors
    public ClassPricingRequest() {}
    
    public ClassPricingRequest(Long classId, String className, Long courseId, String courseName,
                              BigDecimal monthlyPrice, BigDecimal quarterlyPrice, BigDecimal yearlyPrice) {
        this.classId = classId;
        this.className = className;
        this.courseId = courseId;
        this.courseName = courseName;
        this.monthlyPrice = monthlyPrice;
        this.quarterlyPrice = quarterlyPrice;
        this.yearlyPrice = yearlyPrice;
    }
    
    // Getters and Setters
    public Long getClassId() {
        return classId;
    }
    
    public void setClassId(Long classId) {
        this.classId = classId;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
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
    
    @Override
    public String toString() {
        return "ClassPricingRequest{" +
                "classId=" + classId +
                ", className='" + className + '\'' +
                ", courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", monthlyPrice=" + monthlyPrice +
                ", quarterlyPrice=" + quarterlyPrice +
                ", yearlyPrice=" + yearlyPrice +
                ", monthlyDiscountPercent=" + monthlyDiscountPercent +
                ", quarterlyDiscountPercent=" + quarterlyDiscountPercent +
                ", yearlyDiscountPercent=" + yearlyDiscountPercent +
                ", isActive=" + isActive +
                '}';
    }
}
