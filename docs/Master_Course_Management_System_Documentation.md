# Master Course Management System Documentation

## Table of Contents
1. [Overview](#overview)
2. [System Architecture](#system-architecture)
3. [Core Entities](#core-entities)
4. [Course Type Management](#course-type-management)
5. [Course Management](#course-management)
6. [Subject Management](#subject-management)
7. [Class/Exam Management](#classexam-management)
8. [Subject Relationship Management](#subject-relationship-management)
9. [Content Hierarchy Management](#content-hierarchy-management)
10. [Entity Relationships](#entity-relationships)
11. [Database Schema](#database-schema)
12. [Usage Examples](#usage-examples)
13. [Workflow Diagrams](#workflow-diagrams)

---

## Overview

The Master Course Management System provides a comprehensive solution for managing educational content from the top-level course types down to individual chapters. The system supports three distinct course structures: Academic, Competitive, and Professional courses.

### Key Features
- ✅ **Multi-Structure Support**: Academic, Competitive, and Professional course types
- ✅ **Flexible Subject Management**: Master subjects with course-specific mappings
- ✅ **Dynamic Content Hierarchy**: Topics → Modules → Chapters
- ✅ **Relationship-Based Topics**: Topics linked to ClassSubject/ExamSubject/CourseSubject
- ✅ **Multi-Media Content**: YouTube links and file uploads for chapters
- ✅ **Comprehensive Audit Trail**: Complete tracking of all content changes

### System Benefits
- **Scalable Architecture**: Handles multiple course types and structures
- **Flexible Content Organization**: Dynamic hierarchy based on course type
- **Reusable Content**: Topics can be shared across different contexts
- **Rich Media Support**: Integrated multimedia content management
- **Complete Traceability**: Full audit trail for content management

---

## System Architecture

### Course Type Structure
```
CourseType (1) → Course (N) → [ClassEntity/Exam] (N) → Subject (N) → Topic (N) → Module (N) → Chapter (N)
```

### Three Course Types:
1. **Academic (ID: 1)**: Traditional school/college courses
   - Structure: Course → Class → ClassSubject → Topic → Module → Chapter
2. **Competitive (ID: 2)**: Competitive exam preparation
   - Structure: Course → Exam → ExamSubject → Topic → Module → Chapter
3. **Professional (ID: 3)**: Professional development courses
   - Structure: Course → CourseSubject → Topic → Module → Chapter

---

## Core Entities

### 1. CourseType Entity
**Purpose**: Defines the type and structure of courses

**Key Fields**:
```java
- id: Long (Primary Key)
- name: String (e.g., "Academic", "Competitive", "Professional")
- description: String (Detailed description)
- structureType: StructureType (Enum: ACADEMIC, COMPETITIVE, PROFESSIONAL)
- displayOrder: Integer (Order of display)
- isActive: Boolean (Active status)
```

**Relationships**:
- `@OneToMany` → Course (Courses of this type)

### 2. Course Entity
**Purpose**: Main course container

**Key Fields**:
```java
- id: Long (Primary Key)
- name: String (Course name)
- description: String (Course description)
- courseType: CourseType (Type of course)
- displayOrder: Integer (Display order)
- isActive: Boolean (Active status)
```

**Relationships**:
- `@ManyToOne` → CourseType (Parent course type)
- `@OneToMany` → ClassEntity (For Academic courses)
- `@OneToMany` → Exam (For Competitive courses)
- `@OneToMany` → CourseSubject (For Professional courses)

### 3. Subject Entity
**Purpose**: Master subject definitions

**Key Fields**:
```java
- id: Long (Primary Key)
- name: String (Subject name)
- subjectType: String (Subject type/category)
- description: String (Subject description)
- displayOrder: Integer (Display order)
- isActive: Boolean (Active status)
```

**Relationships**:
- `@OneToMany` → ClassSubject (Academic subject mappings)
- `@OneToMany` → ExamSubject (Competitive subject mappings)
- `@OneToMany` → CourseSubject (Professional subject mappings)

---

## Course Type Management

### Academic Course Structure
```
CourseType (Academic) → Course → ClassEntity → ClassSubject → Subject → Topic → Module → Chapter
```

**Flow**:
1. Create Academic CourseType
2. Create Course under Academic type
3. Create ClassEntity (e.g., "Class 10", "Class 12")
4. Map ClassEntity to Subjects via ClassSubject
5. Create Topics linked to ClassSubject
6. Create Modules under Topics
7. Create Chapters under Modules

### Competitive Course Structure
```
CourseType (Competitive) → Course → Exam → ExamSubject → Subject → Topic → Module → Chapter
```

**Flow**:
1. Create Competitive CourseType
2. Create Course under Competitive type
3. Create Exam (e.g., "JEE Main", "NEET")
4. Map Exam to Subjects via ExamSubject
5. Create Topics linked to ExamSubject
6. Create Modules under Topics
7. Create Chapters under Modules

### Professional Course Structure
```
CourseType (Professional) → Course → CourseSubject → Subject → Topic → Module → Chapter
```

**Flow**:
1. Create Professional CourseType
2. Create Course under Professional type
3. Map Course to Subjects via CourseSubject
4. Create Topics linked to CourseSubject
5. Create Modules under Topics
6. Create Chapters under Modules

---

## Course Management

### Course Entity Details
**Purpose**: Central course management

**Key Features**:
- **Multi-Type Support**: Can be Academic, Competitive, or Professional
- **Flexible Structure**: Adapts based on course type
- **Audit Trail**: Complete tracking of changes
- **Display Management**: Ordering and active status

**Relationships**:
- **Academic**: Course → ClassEntity → ClassSubject
- **Competitive**: Course → Exam → ExamSubject
- **Professional**: Course → CourseSubject

### Course Creation Workflow
```
1. Select CourseType (Academic/Competitive/Professional)
2. Create Course with basic details
3. Based on CourseType:
   - Academic: Create ClassEntity → Map to Subjects
   - Competitive: Create Exam → Map to Subjects
   - Professional: Map directly to Subjects
4. Create content hierarchy: Topic → Module → Chapter
```

---

## Subject Management

### Master Subject System
**Purpose**: Centralized subject management with course-specific mappings

**Key Features**:
- **Master Subject List**: Central repository of all subjects
- **Course-Specific Mapping**: Subjects mapped to different course contexts
- **Flexible Relationships**: Same subject can be used across different courses
- **Type Classification**: Subjects categorized by type

### Subject Mapping Entities

#### 1. ClassSubject Entity
**Purpose**: Maps subjects to classes in Academic courses

**Key Fields**:
```java
- id: Long (Primary Key)
- classEntity: ClassEntity (The class)
- subject: Subject (The subject)
- isCompulsory: Boolean (Whether subject is compulsory)
- displayOrder: Integer (Display order)
- isActive: Boolean (Active status)
```

**Unique Constraint**: `(class_id, subject_id)`

#### 2. ExamSubject Entity
**Purpose**: Maps subjects to exams in Competitive courses

**Key Fields**:
```java
- id: Long (Primary Key)
- exam: Exam (The exam)
- subject: Subject (The subject)
- isCompulsory: Boolean (Whether subject is compulsory)
- displayOrder: Integer (Display order)
- isActive: Boolean (Active status)
```

**Unique Constraint**: `(exam_id, subject_id)`

#### 3. CourseSubject Entity
**Purpose**: Maps subjects to courses in Professional courses

**Key Fields**:
```java
- id: Long (Primary Key)
- course: Course (The course)
- subject: Subject (The subject)
- isCompulsory: Boolean (Whether subject is compulsory)
- displayOrder: Integer (Display order)
- isActive: Boolean (Active status)
```

**Unique Constraint**: `(course_id, subject_id)`

---

## Class/Exam Management

### ClassEntity (Academic Courses)
**Purpose**: Represents classes in academic courses

**Key Fields**:
```java
- id: Long (Primary Key)
- name: String (Class name, e.g., "Class 10", "Class 12")
- description: String (Class description)
- course: Course (Parent course)
- displayOrder: Integer (Display order)
- isActive: Boolean (Active status)
```

**Relationships**:
- `@ManyToOne` → Course (Parent course)
- `@OneToMany` → ClassSubject (Subject mappings)

### Exam Entity (Competitive Courses)
**Purpose**: Represents exams in competitive courses

**Key Fields**:
```java
- id: Long (Primary Key)
- name: String (Exam name, e.g., "JEE Main", "NEET")
- description: String (Exam description)
- course: Course (Parent course)
- displayOrder: Integer (Display order)
- isActive: Boolean (Active status)
```

**Relationships**:
- `@ManyToOne` → Course (Parent course)
- `@OneToMany` → ExamSubject (Subject mappings)

---

## Subject Relationship Management

### Topic Entity (Single Table Approach)
**Purpose**: Centralized topic management with dynamic relationships

**Key Fields**:
```java
- id: Long (Primary Key)
- name: String (Topic name)
- description: String (Topic description)
- courseTypeId: Long (1=Academic, 2=Competitive, 3=Professional)
- relationshipId: Long (ID from ClassSubject/ExamSubject/CourseSubject)
- displayOrder: Integer (Display order)
- isActive: Boolean (Active status)
```

**Dynamic Relationship Resolution**:
- **courseTypeId = 1**: relationshipId refers to ClassSubject.id
- **courseTypeId = 2**: relationshipId refers to ExamSubject.id
- **courseTypeId = 3**: relationshipId refers to CourseSubject.id

**Relationships**:
- `@OneToMany` → Module (Modules under this topic)

### Topic Resolution Logic
```java
public Subject resolveSubject() {
    if (courseTypeId == 1) {
        // Academic: Get subject from ClassSubject
        ClassSubject classSubject = classSubjectRepository.findById(relationshipId);
        return classSubject.getSubject();
    } else if (courseTypeId == 2) {
        // Competitive: Get subject from ExamSubject
        ExamSubject examSubject = examSubjectRepository.findById(relationshipId);
        return examSubject.getSubject();
    } else if (courseTypeId == 3) {
        // Professional: Get subject from CourseSubject
        CourseSubject courseSubject = courseSubjectRepository.findById(relationshipId);
        return courseSubject.getSubject();
    }
    return null;
}
```

---

## Content Hierarchy Management

### Module Entity
**Purpose**: Intermediate content organization between Topics and Chapters

**Key Fields**:
```java
- id: Long (Primary Key)
- name: String (Module name)
- description: String (Module description)
- topic: Topic (Parent topic)
- displayOrder: Integer (Display order)
- isActive: Boolean (Active status)
```

**Relationships**:
- `@ManyToOne` → Topic (Parent topic)
- `@OneToMany` → Chapter (Chapters under this module)

### Chapter Entity
**Purpose**: Final content container with multimedia support

**Key Fields**:
```java
- id: Long (Primary Key)
- name: String (Chapter name)
- description: String (Chapter description)
- module: Module (Parent module)
- displayOrder: Integer (Display order)
- isActive: Boolean (Active status)
```

**Relationships**:
- `@ManyToOne` → Module (Parent module)
- `@OneToMany` → ChapterYoutubeLink (YouTube content)
- `@OneToMany` → ChapterUploadedFile (File uploads)
- `@OneToMany` → Question (Questions in this chapter)

### Multimedia Content Support

#### ChapterYoutubeLink Entity
**Purpose**: Manages YouTube video content for chapters

**Key Fields**:
```java
- id: Long (Primary Key)
- chapter: Chapter (Parent chapter)
- title: String (Video title)
- youtubeUrl: String (YouTube URL)
- description: String (Video description)
- displayOrder: Integer (Display order)
- isActive: Boolean (Active status)
```

#### ChapterUploadedFile Entity
**Purpose**: Manages file uploads for chapters

**Key Fields**:
```java
- id: Long (Primary Key)
- chapter: Chapter (Parent chapter)
- fileName: String (Original file name)
- filePath: String (Server file path)
- fileSize: Long (File size in bytes)
- mimeType: String (File MIME type)
- displayOrder: Integer (Display order)
- isActive: Boolean (Active status)
```

---

## Entity Relationships

### Complete Relationship Map
```
CourseType (1) ←→ (N) Course
Course (1) ←→ (N) ClassEntity (Academic)
Course (1) ←→ (N) Exam (Competitive)
Course (1) ←→ (N) CourseSubject (Professional)

ClassEntity (1) ←→ (N) ClassSubject
Exam (1) ←→ (N) ExamSubject
Course (1) ←→ (N) CourseSubject

Subject (1) ←→ (N) ClassSubject
Subject (1) ←→ (N) ExamSubject
Subject (1) ←→ (N) CourseSubject

Topic (N) ←→ (1) ClassSubject/ExamSubject/CourseSubject (via courseTypeId + relationshipId)
Topic (1) ←→ (N) Module
Module (1) ←→ (N) Chapter
Chapter (1) ←→ (N) ChapterYoutubeLink
Chapter (1) ←→ (N) ChapterUploadedFile
Chapter (1) ←→ (N) Question
```

### Relationship Resolution Flow
```
1. CourseType determines structure
2. Course contains ClassEntity/Exam/CourseSubject
3. ClassEntity/Exam/CourseSubject maps to Subject
4. Topic links to ClassSubject/ExamSubject/CourseSubject via courseTypeId + relationshipId
5. Module belongs to Topic
6. Chapter belongs to Module
7. Chapter contains multimedia content and questions
```

---

## Database Schema

### Key Tables
1. **course_types** - Course type definitions
2. **courses** - Main course containers
3. **subjects** - Master subject list
4. **classes** - Academic class entities
5. **exams** - Competitive exam entities
6. **class_subjects** - Academic subject mappings
7. **exam_subjects** - Competitive subject mappings
8. **course_subjects** - Professional subject mappings
9. **topics** - Centralized topic management
10. **modules** - Module containers
11. **chapters** - Chapter containers
12. **chapter_youtube_links** - YouTube content
13. **chapter_uploaded_files** - File uploads

### Key Constraints
- Unique constraints on subject mappings (class-subject, exam-subject, course-subject)
- Foreign key constraints maintaining referential integrity
- Cascade operations for content hierarchy
- Audit trail fields (created_at, updated_at, created_by, updated_by)

---

## Usage Examples

### 1. Creating Academic Course Structure
```java
// 1. Create CourseType
CourseType academicType = new CourseType("Academic", "Traditional school courses", StructureType.ACADEMIC);

// 2. Create Course
Course class10Course = new Course("Class 10 CBSE", "CBSE Class 10 curriculum", academicType);

// 3. Create ClassEntity
ClassEntity class10 = new ClassEntity("Class 10", "CBSE Class 10", class10Course);

// 4. Create Subject
Subject mathematics = new Subject("Mathematics", "MATH", "Mathematical concepts");

// 5. Map Class to Subject
ClassSubject class10Math = new ClassSubject(class10, mathematics);

// 6. Create Topic
Topic algebra = new Topic("Algebra", "Algebraic concepts", 1L, class10Math.getId());

// 7. Create Module
Module linearEquations = new Module("Linear Equations", "Solving linear equations", algebra);

// 8. Create Chapter
Chapter chapter1 = new Chapter("Introduction to Linear Equations", "Basic concepts", linearEquations);
```

### 2. Creating Competitive Course Structure
```java
// 1. Create CourseType
CourseType competitiveType = new CourseType("Competitive", "Competitive exam preparation", StructureType.COMPETITIVE);

// 2. Create Course
Course jeeCourse = new Course("JEE Preparation", "JEE Main and Advanced preparation", competitiveType);

// 3. Create Exam
Exam jeeMain = new Exam("JEE Main", "Joint Entrance Examination Main", jeeCourse);

// 4. Create Subject
Subject physics = new Subject("Physics", "PHY", "Physics concepts");

// 5. Map Exam to Subject
ExamSubject jeePhysics = new ExamSubject(jeeMain, physics);

// 6. Create Topic
Topic mechanics = new Topic("Mechanics", "Mechanical concepts", 2L, jeePhysics.getId());

// 7. Create Module
Module kinematics = new Module("Kinematics", "Motion concepts", mechanics);

// 8. Create Chapter
Chapter motion = new Chapter("Motion in One Dimension", "1D motion concepts", kinematics);
```

### 3. Creating Professional Course Structure
```java
// 1. Create CourseType
CourseType professionalType = new CourseType("Professional", "Professional development", StructureType.PROFESSIONAL);

// 2. Create Course
Course javaCourse = new Course("Java Programming", "Complete Java development course", professionalType);

// 3. Create Subject
Subject programming = new Subject("Programming", "PROG", "Programming concepts");

// 4. Map Course to Subject
CourseSubject javaProgramming = new CourseSubject(javaCourse, programming);

// 5. Create Topic
Topic oop = new Topic("Object-Oriented Programming", "OOP concepts", 3L, javaProgramming.getId());

// 6. Create Module
Module inheritance = new Module("Inheritance", "Inheritance concepts", oop);

// 7. Create Chapter
Chapter polymorphism = new Chapter("Polymorphism", "Polymorphism concepts", inheritance);
```

### 4. Adding Multimedia Content
```java
// Add YouTube video to chapter
ChapterYoutubeLink video = new ChapterYoutubeLink();
video.setChapter(chapter1);
video.setTitle("Introduction to Linear Equations");
video.setYoutubeUrl("https://www.youtube.com/watch?v=example");
video.setDescription("Basic introduction video");

// Add file upload to chapter
ChapterUploadedFile file = new ChapterUploadedFile();
file.setChapter(chapter1);
file.setFileName("linear_equations_notes.pdf");
file.setFilePath("/uploads/chapters/linear_equations_notes.pdf");
file.setFileSize(1024000L);
file.setMimeType("application/pdf");
```

### 5. Topic Resolution Example
```java
// Resolve subject from topic
public Subject resolveSubjectFromTopic(Topic topic) {
    if (topic.getCourseTypeId() == 1) {
        // Academic: Get from ClassSubject
        ClassSubject classSubject = classSubjectRepository.findById(topic.getRelationshipId());
        return classSubject.getSubject();
    } else if (topic.getCourseTypeId() == 2) {
        // Competitive: Get from ExamSubject
        ExamSubject examSubject = examSubjectRepository.findById(topic.getRelationshipId());
        return examSubject.getSubject();
    } else if (topic.getCourseTypeId() == 3) {
        // Professional: Get from CourseSubject
        CourseSubject courseSubject = courseSubjectRepository.findById(topic.getRelationshipId());
        return courseSubject.getSubject();
    }
    return null;
}
```

---

## Workflow Diagrams

### Academic Course Creation Flow
```
1. Create CourseType (Academic)
   ↓
2. Create Course
   ↓
3. Create ClassEntity (Class 10, Class 12, etc.)
   ↓
4. Create/Select Subject
   ↓
5. Create ClassSubject (Map Class to Subject)
   ↓
6. Create Topic (Link to ClassSubject)
   ↓
7. Create Module (Under Topic)
   ↓
8. Create Chapter (Under Module)
   ↓
9. Add Multimedia Content (YouTube, Files)
```

### Competitive Course Creation Flow
```
1. Create CourseType (Competitive)
   ↓
2. Create Course
   ↓
3. Create Exam (JEE Main, NEET, etc.)
   ↓
4. Create/Select Subject
   ↓
5. Create ExamSubject (Map Exam to Subject)
   ↓
6. Create Topic (Link to ExamSubject)
   ↓
7. Create Module (Under Topic)
   ↓
8. Create Chapter (Under Module)
   ↓
9. Add Multimedia Content (YouTube, Files)
```

### Professional Course Creation Flow
```
1. Create CourseType (Professional)
   ↓
2. Create Course
   ↓
3. Create/Select Subject
   ↓
4. Create CourseSubject (Map Course to Subject)
   ↓
5. Create Topic (Link to CourseSubject)
   ↓
6. Create Module (Under Topic)
   ↓
7. Create Chapter (Under Module)
   ↓
8. Add Multimedia Content (YouTube, Files)
```

---

## Benefits of This System

### For Administrators
- ✅ **Unified Management**: Single system for all course types
- ✅ **Flexible Structure**: Adapts to different educational models
- ✅ **Content Reusability**: Topics can be shared across contexts
- ✅ **Rich Media Support**: Integrated multimedia management
- ✅ **Complete Audit Trail**: Full tracking of all changes

### For Content Creators
- ✅ **Structured Organization**: Clear hierarchy from course to chapter
- ✅ **Flexible Mapping**: Subjects can be mapped to different contexts
- ✅ **Multimedia Integration**: Easy addition of videos and files
- ✅ **Version Control**: Complete history of content changes

### For System
- ✅ **Scalable Architecture**: Handles multiple course types efficiently
- ✅ **Data Integrity**: Comprehensive constraints and relationships
- ✅ **Performance Optimized**: Efficient querying with proper indexing
- ✅ **Extensible Design**: Easy to add new course types or features

---

This documentation provides a comprehensive overview of the Master Course Management System, covering all entities from CourseType down to Chapter creation. The system is designed to be flexible, scalable, and provide rich content management capabilities for educational institutions.
