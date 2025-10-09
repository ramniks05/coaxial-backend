-- Add status column to student_subscriptions table
ALTER TABLE student_subscriptions ADD COLUMN status VARCHAR(20);

-- Set initial status values based on existing data
-- CANCELLED: if payment_status is CANCELLED or REFUNDED or PARTIALLY_REFUNDED
UPDATE student_subscriptions 
SET status = 'CANCELLED' 
WHERE payment_status IN ('CANCELLED', 'REFUNDED', 'PARTIALLY_REFUNDED');

-- PENDING: if payment_status is PENDING or FAILED
UPDATE student_subscriptions 
SET status = 'PENDING' 
WHERE payment_status IN ('PENDING', 'FAILED') AND status IS NULL;

-- EXPIRED: if payment_status is PAID but end_date < current date
UPDATE student_subscriptions 
SET status = 'EXPIRED' 
WHERE payment_status = 'PAID' 
  AND end_date IS NOT NULL 
  AND end_date < CURRENT_TIMESTAMP 
  AND status IS NULL;

-- ACTIVE: if payment_status is PAID, is_active is true, and (no end_date or end_date >= current date)
UPDATE student_subscriptions 
SET status = 'ACTIVE' 
WHERE payment_status = 'PAID' 
  AND is_active = TRUE 
  AND (end_date IS NULL OR end_date >= CURRENT_TIMESTAMP) 
  AND status IS NULL;

-- Set any remaining records to CANCELLED as fallback
UPDATE student_subscriptions 
SET status = 'CANCELLED' 
WHERE status IS NULL;

-- Create index on status column for filtering performance
CREATE INDEX idx_student_subscriptions_status ON student_subscriptions(status);

-- Create composite index for common queries
CREATE INDEX idx_student_subscriptions_student_status ON student_subscriptions(student_id, status);

