package com.coaxial.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * DTO for Razorpay Webhook events
 * Reference: https://razorpay.com/docs/webhooks/
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RazorpayWebhookDTO {

    private String entity;
    private String account_id;
    private String event;
    private Boolean contains;

    @JsonProperty("payload")
    private WebhookPayload payload;

    @JsonProperty("created_at")
    private Long created_at;

    // Constructors
    public RazorpayWebhookDTO() {
    }

    // Getters and Setters
    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Boolean getContains() {
        return contains;
    }

    public void setContains(Boolean contains) {
        this.contains = contains;
    }

    public WebhookPayload getPayload() {
        return payload;
    }

    public void setPayload(WebhookPayload payload) {
        this.payload = payload;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    /**
     * Inner class for webhook payload
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WebhookPayload {
        
        @JsonProperty("payment")
        private PaymentEntity payment;

        @JsonProperty("order")
        private OrderEntity order;

        public PaymentEntity getPayment() {
            return payment;
        }

        public void setPayment(PaymentEntity payment) {
            this.payment = payment;
        }

        public OrderEntity getOrder() {
            return order;
        }

        public void setOrder(OrderEntity order) {
            this.order = order;
        }
    }

    /**
     * Inner class for payment entity in webhook
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentEntity {
        
        private String id;
        private String entity;
        private Long amount;
        private String currency;
        private String status;
        
        @JsonProperty("order_id")
        private String order_id;
        
        @JsonProperty("invoice_id")
        private String invoice_id;
        
        private Boolean international;
        private String method;
        
        @JsonProperty("amount_refunded")
        private Long amount_refunded;
        
        @JsonProperty("refund_status")
        private String refund_status;
        
        private Boolean captured;
        private String description;
        
        @JsonProperty("card_id")
        private String card_id;
        
        private String bank;
        private String wallet;
        private String vpa;
        private String email;
        private String contact;
        
        @JsonProperty("customer_id")
        private String customer_id;
        
        private Map<String, String> notes;
        
        private Long fee;
        private Long tax;
        
        @JsonProperty("error_code")
        private String error_code;
        
        @JsonProperty("error_description")
        private String error_description;
        
        @JsonProperty("error_source")
        private String error_source;
        
        @JsonProperty("error_step")
        private String error_step;
        
        @JsonProperty("error_reason")
        private String error_reason;
        
        @JsonProperty("created_at")
        private Long created_at;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEntity() {
            return entity;
        }

        public void setEntity(String entity) {
            this.entity = entity;
        }

        public Long getAmount() {
            return amount;
        }

        public void setAmount(Long amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public String getInvoice_id() {
            return invoice_id;
        }

        public void setInvoice_id(String invoice_id) {
            this.invoice_id = invoice_id;
        }

        public Boolean getInternational() {
            return international;
        }

        public void setInternational(Boolean international) {
            this.international = international;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public Long getAmount_refunded() {
            return amount_refunded;
        }

        public void setAmount_refunded(Long amount_refunded) {
            this.amount_refunded = amount_refunded;
        }

        public String getRefund_status() {
            return refund_status;
        }

        public void setRefund_status(String refund_status) {
            this.refund_status = refund_status;
        }

        public Boolean getCaptured() {
            return captured;
        }

        public void setCaptured(Boolean captured) {
            this.captured = captured;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCard_id() {
            return card_id;
        }

        public void setCard_id(String card_id) {
            this.card_id = card_id;
        }

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public String getWallet() {
            return wallet;
        }

        public void setWallet(String wallet) {
            this.wallet = wallet;
        }

        public String getVpa() {
            return vpa;
        }

        public void setVpa(String vpa) {
            this.vpa = vpa;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getCustomer_id() {
            return customer_id;
        }

        public void setCustomer_id(String customer_id) {
            this.customer_id = customer_id;
        }

        public Map<String, String> getNotes() {
            return notes;
        }

        public void setNotes(Map<String, String> notes) {
            this.notes = notes;
        }

        public Long getFee() {
            return fee;
        }

        public void setFee(Long fee) {
            this.fee = fee;
        }

        public Long getTax() {
            return tax;
        }

        public void setTax(Long tax) {
            this.tax = tax;
        }

        public String getError_code() {
            return error_code;
        }

        public void setError_code(String error_code) {
            this.error_code = error_code;
        }

        public String getError_description() {
            return error_description;
        }

        public void setError_description(String error_description) {
            this.error_description = error_description;
        }

        public String getError_source() {
            return error_source;
        }

        public void setError_source(String error_source) {
            this.error_source = error_source;
        }

        public String getError_step() {
            return error_step;
        }

        public void setError_step(String error_step) {
            this.error_step = error_step;
        }

        public String getError_reason() {
            return error_reason;
        }

        public void setError_reason(String error_reason) {
            this.error_reason = error_reason;
        }

        public Long getCreated_at() {
            return created_at;
        }

        public void setCreated_at(Long created_at) {
            this.created_at = created_at;
        }
    }

    /**
     * Inner class for order entity in webhook
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderEntity {
        
        private String id;
        private String entity;
        private Long amount;
        
        @JsonProperty("amount_paid")
        private Long amount_paid;
        
        @JsonProperty("amount_due")
        private Long amount_due;
        
        private String currency;
        private String receipt;
        private String status;
        private Integer attempts;
        private Map<String, String> notes;
        
        @JsonProperty("created_at")
        private Long created_at;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEntity() {
            return entity;
        }

        public void setEntity(String entity) {
            this.entity = entity;
        }

        public Long getAmount() {
            return amount;
        }

        public void setAmount(Long amount) {
            this.amount = amount;
        }

        public Long getAmount_paid() {
            return amount_paid;
        }

        public void setAmount_paid(Long amount_paid) {
            this.amount_paid = amount_paid;
        }

        public Long getAmount_due() {
            return amount_due;
        }

        public void setAmount_due(Long amount_due) {
            this.amount_due = amount_due;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getReceipt() {
            return receipt;
        }

        public void setReceipt(String receipt) {
            this.receipt = receipt;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getAttempts() {
            return attempts;
        }

        public void setAttempts(Integer attempts) {
            this.attempts = attempts;
        }

        public Map<String, String> getNotes() {
            return notes;
        }

        public void setNotes(Map<String, String> notes) {
            this.notes = notes;
        }

        public Long getCreated_at() {
            return created_at;
        }

        public void setCreated_at(Long created_at) {
            this.created_at = created_at;
        }
    }
}

