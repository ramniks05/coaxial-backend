package com.coaxial.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Request DTO for setting exam pricing in admin pricing management
 */
public class ExamPricingRequest {
    
    @NotNull(message = "Exam ID is required")
    private Long examId;
    
    @NotBlank(message = "Exam name is required")
    private String examName;
    
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
    public ExamPricingRequest() {}
    
    public ExamPricingRequest(Long examId, String examName, BigDecimal monthlyPrice, 
                             BigDecimal quarterlyPrice, BigDecimal yearlyPrice) {
        this.examId = examId;
        this.examName = examName;
        this.monthlyPrice = monthlyPrice;
        this.quarterlyPrice = quarterlyPrice;
        this.yearlyPrice = yearlyPrice;
    }
    
    // Getters and Setters
    public Long getExamId() {
        return examId;
    }
    
    public void setExamId(Long examId) {
        this.examId = examId;
    }
    
    public String getExamName() {
        return examName;
    }
    
    public void setExamName(String examName) {
        this.examName = examName;
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
        return "ExamPricingRequest{" +
                "examId=" + examId +
                ", examName='" + examName + '\'' +
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
