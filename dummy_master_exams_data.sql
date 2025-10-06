-- Dummy data for master_exams table (10 records)
-- Note: Adjust the ID values based on your existing data to avoid conflicts

INSERT INTO public.master_exams(
    id, created_at, description, exam_name, is_active, updated_at, created_by, updated_by)
VALUES 
    (1, NOW(), 'Comprehensive assessment covering fundamental concepts in mathematics, physics, and chemistry for engineering students.', 'Engineering Entrance Exam 2024', true, NOW(), 'admin', 'admin'),
    
    (2, NOW(), 'Standardized test for medical college admissions covering biology, chemistry, and physics with focus on healthcare applications.', 'Medical College Admission Test', true, NOW(), 'admin', 'admin'),
    
    (3, NOW(), 'Professional certification exam for software engineers covering programming languages, algorithms, and system design.', 'Software Engineering Certification', true, NOW(), 'admin', 'admin'),
    
    (4, NOW(), 'Business management assessment evaluating analytical skills, decision-making, and strategic thinking capabilities.', 'MBA Entrance Examination', true, NOW(), 'admin', 'admin'),
    
    (5, NOW(), 'Legal profession entrance test covering constitutional law, civil law, criminal law, and legal reasoning.', 'Law School Admission Test', true, NOW(), 'admin', 'admin'),
    
    (6, NOW(), 'Architecture and design assessment evaluating creativity, technical skills, and spatial reasoning abilities.', 'Architecture Entrance Exam', true, NOW(), 'admin', 'admin'),
    
    (7, NOW(), 'Financial services certification covering investment analysis, risk management, and regulatory compliance.', 'Financial Analyst Certification', true, NOW(), 'admin', 'admin'),
    
    (8, NOW(), 'Teaching profession assessment covering pedagogy, subject knowledge, and classroom management skills.', 'Teacher Eligibility Test', true, NOW(), 'admin', 'admin'),
    
    (9, NOW(), 'Veterinary medicine entrance examination covering animal biology, pathology, and clinical practices.', 'Veterinary Medicine Entrance', true, NOW(), 'admin', 'admin'),
    
    (10, NOW(), 'Civil services preparation exam covering general studies, current affairs, and administrative aptitude.', 'Civil Services Preliminary', true, NOW(), 'admin', 'admin');
