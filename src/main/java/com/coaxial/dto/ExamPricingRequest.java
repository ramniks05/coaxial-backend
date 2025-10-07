package com.coaxial.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for setting exam pricing in admin pricing management
 */
public class ExamPricingRequest {
    
    @NotNull(message = "Exam ID is required")
    private Long examId;
    
    @NotBlank(message = "Exam name is required")
    private String examName;
    
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
        return "ExamPricingRequest{" +
                "examId=" + examId +
                ", examName='" + examName + '\'' +
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
