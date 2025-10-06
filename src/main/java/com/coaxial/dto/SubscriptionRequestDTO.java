package com.coaxial.dto;

import com.coaxial.enums.SubscriptionLevel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public class SubscriptionRequestDTO {

    @NotNull(message = "Subscription level is required")
    private SubscriptionLevel subscriptionLevel;

    @NotNull(message = "Entity ID is required")
    @Positive(message = "Entity ID must be positive")
    private Long entityId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    private String notes;

    // Constructors
    public SubscriptionRequestDTO() {
    }

    public SubscriptionRequestDTO(SubscriptionLevel subscriptionLevel, Long entityId, BigDecimal amount, Integer durationDays) {
        this.subscriptionLevel = subscriptionLevel;
        this.entityId = entityId;
        this.amount = amount;
        this.durationDays = durationDays;
    }

    // Getters and Setters
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
