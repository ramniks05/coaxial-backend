-- Migration to insert master data for course types, subjects, exams, and years
-- This will initialize the system with course types, academic subjects, competitive exam subjects, and popular exams

-- =====================================================
-- INSERT COURSE TYPES (REQUIRED FIRST)
-- =====================================================
-- The 3 essential course types that the system requires

INSERT INTO course_types (name, description, structure_type, display_order, is_active, created_at, updated_at)
VALUES 
    ('Academic', 'Traditional school and college courses', 'ACADEMIC', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Competitive', 'Competitive exam preparation courses', 'COMPETITIVE', 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Professional', 'Professional skill development courses', 'PROFESSIONAL', 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- INSERT MASTER SUBJECTS FOR ACADEMIC COURSES
-- =====================================================
-- Academic subjects (course_type_id = 1)

INSERT INTO subjects (name, description, course_type_id, display_order, is_active, created_at, updated_at, created_by)
VALUES 
    ('Mathematics', 'Core mathematical concepts including algebra, geometry, calculus and statistics', 1, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM'),
    ('Science', 'General science covering physics, chemistry, and biology fundamentals', 1, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM'),
    ('English', 'English language, literature, grammar, and communication skills', 1, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM'),
    ('Social Studies', 'History, geography, civics, and social sciences', 1, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM'),
    ('Computer Science', 'Programming, algorithms, data structures, and computer fundamentals', 1, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM')
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- INSERT MASTER SUBJECTS FOR COMPETITIVE EXAMS
-- =====================================================
-- Competitive exam subjects (course_type_id = 2)

INSERT INTO subjects (name, description, course_type_id, display_order, is_active, created_at, updated_at, created_by)
VALUES 
    ('Quantitative Aptitude', 'Numerical ability, data interpretation, and mathematical reasoning', 2, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM'),
    ('Logical Reasoning', 'Analytical reasoning, logical deductions, and problem-solving skills', 2, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM'),
    ('Verbal Ability', 'English comprehension, vocabulary, grammar, and verbal reasoning', 2, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM'),
    ('General Knowledge', 'Current affairs, general awareness, and static GK', 2, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM'),
    ('Technical Aptitude', 'Domain-specific technical knowledge and reasoning', 2, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM')
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- INSERT MASTER EXAMS
-- =====================================================
-- Popular competitive exams

INSERT INTO master_exams (exam_name, description, is_active, created_at, updated_at)
VALUES 
    ('JEE Main', 'Joint Entrance Examination (Main) for engineering admissions', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('JEE Advanced', 'Joint Entrance Examination (Advanced) for IIT admissions', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('NEET', 'National Eligibility cum Entrance Test for medical admissions', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('UPSC CSE', 'Union Public Service Commission Civil Services Examination', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SSC CGL', 'Staff Selection Commission Combined Graduate Level Examination', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('GATE', 'Graduate Aptitude Test in Engineering', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CAT', 'Common Admission Test for MBA programs', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CLAT', 'Common Law Admission Test for law programs', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('NDA', 'National Defence Academy entrance examination', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('IBPS PO', 'Institute of Banking Personnel Selection - Probationary Officer', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- INSERT MASTER YEARS
-- =====================================================
-- Insert common academic years from 2020 to 2030

INSERT INTO master_years (year_value, is_active, created_at)
VALUES 
    (2020, true, CURRENT_TIMESTAMP),
    (2021, true, CURRENT_TIMESTAMP),
    (2022, true, CURRENT_TIMESTAMP),
    (2023, true, CURRENT_TIMESTAMP),
    (2024, true, CURRENT_TIMESTAMP),
    (2025, true, CURRENT_TIMESTAMP),
    (2026, true, CURRENT_TIMESTAMP),
    (2027, true, CURRENT_TIMESTAMP),
    (2028, true, CURRENT_TIMESTAMP),
    (2029, true, CURRENT_TIMESTAMP),
    (2030, true, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- VERIFICATION QUERIES (COMMENTED OUT)
-- =====================================================
-- Uncomment these to verify the data was inserted correctly

-- SELECT * FROM course_types ORDER BY display_order;
-- SELECT * FROM subjects WHERE course_type_id = 1 ORDER BY display_order;
-- SELECT * FROM subjects WHERE course_type_id = 2 ORDER BY display_order;
-- SELECT * FROM master_exams WHERE is_active = true ORDER BY exam_name;
-- SELECT * FROM master_years ORDER BY year_value;

