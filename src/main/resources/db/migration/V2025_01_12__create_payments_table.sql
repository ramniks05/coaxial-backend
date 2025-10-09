-- Create payments table to separate payment tracking from subscriptions
-- This prevents duplicate subscriptions on payment failures

CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    
    -- Payment identification
    payment_reference VARCHAR(100) UNIQUE,
    payment_type VARCHAR(20) NOT NULL,  -- SUBSCRIPTION, RENEWAL, UPGRADE
    
    -- Related entities
    student_id BIGINT NOT NULL,
    subscription_id BIGINT,  -- NULL for new subscription, populated for renewal
    
    -- Subscription details (for creating subscription after payment)
    subscription_level VARCHAR(20),
    entity_id BIGINT,
    entity_name VARCHAR(255),
    plan_type VARCHAR(20),
    duration_days INT,
    
    -- Payment details
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    
    -- Razorpay details
    razorpay_order_id VARCHAR(100) UNIQUE,
    razorpay_payment_id VARCHAR(100),
    razorpay_signature VARCHAR(255),
    razorpay_receipt VARCHAR(100),
    
    -- Status tracking
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING, PAID, FAILED, CANCELLED
    payment_date TIMESTAMP NULL,
    failure_reason TEXT,
    retry_count INT DEFAULT 0,
    
    -- Metadata
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign keys
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_id) REFERENCES student_subscriptions(id) ON DELETE SET NULL,
    
    -- Indexes for performance
    INDEX idx_payments_student (student_id),
    INDEX idx_payments_razorpay_order (razorpay_order_id),
    INDEX idx_payments_razorpay_payment (razorpay_payment_id),
    INDEX idx_payments_status (payment_status),
    INDEX idx_payments_subscription (subscription_id),
    INDEX idx_payments_reference (payment_reference),
    INDEX idx_payments_type (payment_type),
    INDEX idx_payments_created (created_at)
);

-- Add comment to table
ALTER TABLE payments COMMENT = 'Payment records for subscription purchases and renewals. Prevents duplicate subscriptions on payment failures.';

