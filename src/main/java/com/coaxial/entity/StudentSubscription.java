package com.coaxial.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.coaxial.enums.PaymentStatus;
import com.coaxial.enums.PlanType;
import com.coaxial.enums.SubscriptionLevel;
import com.coaxial.enums.SubscriptionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "student_subscriptions")
@EntityListeners(AuditingEntityListener.class)
public class StudentSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull(message = "Student is required")
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_level", nullable = false)
    @NotNull(message = "Subscription level is required")
    private SubscriptionLevel subscriptionLevel; // CLASS, EXAM, COURSE

    @Column(name = "entity_id", nullable = false)
    @NotNull(message = "Entity ID is required")
    private Long entityId; // classId, examId, or courseId

    @Column(name = "entity_name")
    private String entityName; // For display purposes

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @NotNull(message = "Currency is required")
    private String currency = "INR";

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type")
    private PlanType planType;

    // Razorpay payment fields
    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature")
    private String razorpaySignature;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "razorpay_receipt")
    private String razorpayReceipt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SubscriptionStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public StudentSubscription() {
    }

    public StudentSubscription(User student, SubscriptionLevel subscriptionLevel, Long entityId, 
                              String entityName, BigDecimal amount, Integer durationDays) {
        this.student = student;
        this.subscriptionLevel = subscriptionLevel;
        this.entityId = entityId;
        this.entityName = entityName;
        this.amount = amount;
        this.durationDays = durationDays;
        this.startDate = LocalDateTime.now();
        if (durationDays != null) {
            this.endDate = this.startDate.plusDays(durationDays);
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public SubscriptionLevel getSubscriptionLevel() {
        return subscriptionLevel;
    }

    public void setSubscriptionLevel(SubscriptionLevel subscriptionLevel) {
        this.subscriptionLevel = subscriptionLevel;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
        if (durationDays != null && this.startDate != null) {
            this.endDate = this.startDate.plusDays(durationDays);
        }
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getRazorpayReceipt() {
        return razorpayReceipt;
    }

    public void setRazorpayReceipt(String razorpayReceipt) {
        this.razorpayReceipt = razorpayReceipt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
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

    // Utility methods
    public boolean isExpired() {
        return endDate != null && LocalDateTime.now().isAfter(endDate);
    }

    public boolean isActiveAndNotExpired() {
        return isActive && !isExpired();
    }

    public long getRemainingDays() {
        if (endDate == null) return -1;
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(endDate)) return 0;
        return java.time.Duration.between(now, endDate).toDays();
    }

    /**
     * Compute the current subscription status based on payment status, active flag, and expiry date
     */
    public SubscriptionStatus computeStatus() {
        if (paymentStatus == PaymentStatus.CANCELLED) {
            return SubscriptionStatus.CANCELLED;
        }
        if (paymentStatus != PaymentStatus.PAID) {
            return SubscriptionStatus.PENDING;
        }
        if (isExpired()) {
            return SubscriptionStatus.EXPIRED;
        }
        if (isActive) {
            return SubscriptionStatus.ACTIVE;
        }
        return SubscriptionStatus.CANCELLED;
    }

    /**
     * Get the expiry date calculated from start date and duration
     */
    public LocalDateTime getExpiryDate() {
        if (endDate != null) {
            return endDate;
        }
        if (startDate != null && durationDays != null) {
            return startDate.plusDays(durationDays);
        }
        return null;
    }
}
