-- Database indexes for Master Data endpoints optimization
-- These indexes will improve query performance for the standardized endpoints

-- Course Types indexes
CREATE INDEX IF NOT EXISTS idx_course_types_active ON course_types(is_active);

-- Courses indexes
CREATE INDEX IF NOT EXISTS idx_courses_course_type ON courses(course_type_id);
CREATE INDEX IF NOT EXISTS idx_courses_active ON courses(is_active);
CREATE INDEX IF NOT EXISTS idx_courses_course_type_active ON courses(course_type_id, is_active);

-- Classes indexes
CREATE INDEX IF NOT EXISTS idx_classes_course ON classes(course_id);
CREATE INDEX IF NOT EXISTS idx_classes_active ON classes(is_active);
CREATE INDEX IF NOT EXISTS idx_classes_course_active ON classes(course_id, is_active);
CREATE INDEX IF NOT EXISTS idx_classes_course_type ON classes(course_type_id);
CREATE INDEX IF NOT EXISTS idx_classes_course_type_course ON classes(course_type_id, course_id);

-- Exams indexes
CREATE INDEX IF NOT EXISTS idx_exams_course ON exams(course_id);
CREATE INDEX IF NOT EXISTS idx_exams_active ON exams(is_active);
CREATE INDEX IF NOT EXISTS idx_exams_course_active ON exams(course_id, is_active);
CREATE INDEX IF NOT EXISTS idx_exams_course_type ON exams(course_type_id);
CREATE INDEX IF NOT EXISTS idx_exams_course_type_course ON exams(course_type_id, course_id);

-- Master Subjects (subjects table) indexes
CREATE INDEX IF NOT EXISTS idx_subjects_course_type ON subjects(course_type_id);
CREATE INDEX IF NOT EXISTS idx_subjects_active ON subjects(is_active);
CREATE INDEX IF NOT EXISTS idx_subjects_course_type_active ON subjects(course_type_id, is_active);
CREATE INDEX IF NOT EXISTS idx_subjects_display_order ON subjects(display_order);

-- Subject Linkages indexes
-- Class Subject linkages
CREATE INDEX IF NOT EXISTS idx_class_subjects_class ON class_subjects(class_id);
CREATE INDEX IF NOT EXISTS idx_class_subjects_subject ON class_subjects(subject_id);
CREATE INDEX IF NOT EXISTS idx_class_subjects_class_subject ON class_subjects(class_id, subject_id);

-- Exam Subject linkages
CREATE INDEX IF NOT EXISTS idx_exam_subjects_exam ON exam_subjects(exam_id);
CREATE INDEX IF NOT EXISTS idx_exam_subjects_subject ON exam_subjects(subject_id);
CREATE INDEX IF NOT EXISTS idx_exam_subjects_exam_subject ON exam_subjects(exam_id, subject_id);

-- Course Subject linkages
CREATE INDEX IF NOT EXISTS idx_course_subjects_course ON course_subjects(course_id);
CREATE INDEX IF NOT EXISTS idx_course_subjects_subject ON course_subjects(subject_id);
CREATE INDEX IF NOT EXISTS idx_course_subjects_course_subject ON course_subjects(course_id, subject_id);

-- Topics indexes
CREATE INDEX IF NOT EXISTS idx_topics_subject ON topics(subject_id);
CREATE INDEX IF NOT EXISTS idx_topics_active ON topics(is_active);
CREATE INDEX IF NOT EXISTS idx_topics_subject_active ON topics(subject_id, is_active);
CREATE INDEX IF NOT EXISTS idx_topics_display_order ON topics(display_order);

-- Modules indexes
CREATE INDEX IF NOT EXISTS idx_modules_topic ON modules(topic_id);
CREATE INDEX IF NOT EXISTS idx_modules_active ON modules(is_active);
CREATE INDEX IF NOT EXISTS idx_modules_topic_active ON modules(topic_id, is_active);
CREATE INDEX IF NOT EXISTS idx_modules_display_order ON modules(display_order);

-- Chapters indexes
CREATE INDEX IF NOT EXISTS idx_chapters_module ON chapters(module_id);
CREATE INDEX IF NOT EXISTS idx_chapters_active ON chapters(is_active);
CREATE INDEX IF NOT EXISTS idx_chapters_module_active ON chapters(module_id, is_active);
CREATE INDEX IF NOT EXISTS idx_chapters_display_order ON chapters(display_order);

-- Composite indexes for complex queries
-- For hierarchical filtering (courseType -> course -> class/exam -> subject -> topic -> module -> chapter)
CREATE INDEX IF NOT EXISTS idx_courses_type_active_name ON courses(course_type_id, is_active, name);
CREATE INDEX IF NOT EXISTS idx_classes_course_active_name ON classes(course_id, is_active, name);
CREATE INDEX IF NOT EXISTS idx_exams_course_active_name ON exams(course_id, is_active, name);
CREATE INDEX IF NOT EXISTS idx_subjects_type_active_name ON subjects(course_type_id, is_active, name);
CREATE INDEX IF NOT EXISTS idx_topics_subject_active_name ON topics(subject_id, is_active, name);
CREATE INDEX IF NOT EXISTS idx_modules_topic_active_name ON modules(topic_id, is_active, name);
CREATE INDEX IF NOT EXISTS idx_chapters_module_active_name ON chapters(module_id, is_active, name);

-- Search optimization indexes (for text search)
CREATE INDEX IF NOT EXISTS idx_courses_name_trgm ON courses USING gin(name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_classes_name_trgm ON classes USING gin(name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_exams_name_trgm ON exams USING gin(name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_subjects_name_trgm ON subjects USING gin(name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_topics_name_trgm ON topics USING gin(name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_modules_name_trgm ON modules USING gin(name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_chapters_name_trgm ON chapters USING gin(name gin_trgm_ops);

-- Description search indexes (optional, for description search)
CREATE INDEX IF NOT EXISTS idx_courses_description_trgm ON courses USING gin(description gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_classes_description_trgm ON classes USING gin(description gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_exams_description_trgm ON exams USING gin(description gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_subjects_description_trgm ON subjects USING gin(description gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_topics_description_trgm ON topics USING gin(description gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_modules_description_trgm ON modules USING gin(description gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_chapters_description_trgm ON chapters USING gin(description gin_trgm_ops);
