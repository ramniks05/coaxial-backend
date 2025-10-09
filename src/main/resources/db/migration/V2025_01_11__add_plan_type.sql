-- Add plan_type column to student_subscriptions table
ALTER TABLE student_subscriptions ADD COLUMN plan_type VARCHAR(20);

-- Set initial plan_type values based on duration_days
-- MONTHLY: <= 31 days
UPDATE student_subscriptions 
SET plan_type = 'MONTHLY' 
WHERE duration_days IS NOT NULL 
  AND duration_days <= 31 
  AND plan_type IS NULL;

-- QUARTERLY: 32-100 days
UPDATE student_subscriptions 
SET plan_type = 'QUARTERLY' 
WHERE duration_days IS NOT NULL 
  AND duration_days > 31 
  AND duration_days <= 100 
  AND plan_type IS NULL;

-- YEARLY: > 100 days
UPDATE student_subscriptions 
SET plan_type = 'YEARLY' 
WHERE duration_days IS NOT NULL 
  AND duration_days > 100 
  AND plan_type IS NULL;

-- Create index on plan_type column for filtering performance
CREATE INDEX idx_student_subscriptions_plan_type ON student_subscriptions(plan_type);

