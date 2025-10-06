package com.coaxial.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

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
    
    @NotNull(message = "Monthly price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal monthlyPrice;
    
    @NotNull(message = "Quarterly price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal quarterlyPrice;
    
    @NotNull(message = "Yearly price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal yearlyPrice;
    
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer monthlyDurationDays = 30;
    
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer quarterlyDurationDays = 90;
    
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer yearlyDurationDays = 365;
    
    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    private Integer quarterlyDiscountPercent = 10;
    
    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    private Integer yearlyDiscountPercent = 20;
    
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
    
    public Integer getMonthlyDurationDays() {
        return monthlyDurationDays;
    }
    
    public void setMonthlyDurationDays(Integer monthlyDurationDays) {
        this.monthlyDurationDays = monthlyDurationDays;
    }
    
    public Integer getQuarterlyDurationDays() {
        return quarterlyDurationDays;
    }
    
    public void setQuarterlyDurationDays(Integer quarterlyDurationDays) {
        this.quarterlyDurationDays = quarterlyDurationDays;
    }
    
    public Integer getYearlyDurationDays() {
        return yearlyDurationDays;
    }
    
    public void setYearlyDurationDays(Integer yearlyDurationDays) {
        this.yearlyDurationDays = yearlyDurationDays;
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
                ", monthlyDurationDays=" + monthlyDurationDays +
                ", quarterlyDurationDays=" + quarterlyDurationDays +
                ", yearlyDurationDays=" + yearlyDurationDays +
                ", quarterlyDiscountPercent=" + quarterlyDiscountPercent +
                ", yearlyDiscountPercent=" + yearlyDiscountPercent +
                ", isActive=" + isActive +
                '}';
    }
}
