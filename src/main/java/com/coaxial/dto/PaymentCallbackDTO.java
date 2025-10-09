package com.coaxial.dto;

import jakarta.validation.constraints.NotBlank;

public class PaymentCallbackDTO {

    @NotBlank(message = "Razorpay order ID is required")
    private String razorpay_order_id;

    @NotBlank(message = "Razorpay payment ID is required")
    private String razorpay_payment_id;

    @NotBlank(message = "Razorpay signature is required")
    private String razorpay_signature;

    // Note: subscriptionId removed - subscription is created AFTER payment verification
    // Backend will find the payment by razorpay_order_id and create subscription

    // Constructors
    public PaymentCallbackDTO() {
    }

    public PaymentCallbackDTO(String razorpay_order_id, String razorpay_payment_id, String razorpay_signature) {
        this.razorpay_order_id = razorpay_order_id;
        this.razorpay_payment_id = razorpay_payment_id;
        this.razorpay_signature = razorpay_signature;
    }

    // Getters and Setters
    public String getRazorpay_order_id() {
        return razorpay_order_id;
    }

    public void setRazorpay_order_id(String razorpay_order_id) {
        this.razorpay_order_id = razorpay_order_id;
    }

    public String getRazorpay_payment_id() {
        return razorpay_payment_id;
    }

    public void setRazorpay_payment_id(String razorpay_payment_id) {
        this.razorpay_payment_id = razorpay_payment_id;
    }

    public String getRazorpay_signature() {
        return razorpay_signature;
    }

    public void setRazorpay_signature(String razorpay_signature) {
        this.razorpay_signature = razorpay_signature;
    }
}
