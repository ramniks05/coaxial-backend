package com.coaxial.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.coaxial.entity.CourseType;
import com.coaxial.entity.MasterExam;
import com.coaxial.entity.MasterYear;
import com.coaxial.entity.StructureType;
import com.coaxial.entity.Subject;
import com.coaxial.repository.CourseTypeRepository;
import com.coaxial.repository.MasterExamRepository;
import com.coaxial.repository.MasterYearRepository;
import com.coaxial.repository.SubjectRepository;

/**
 * Initializes master data (course types, subjects, exams, and years) when the application starts.
 * This ensures the system has essential reference data available from the first run.
 */
@Component
public class MasterDataInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(MasterDataInitializer.class);

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MasterYearRepository masterYearRepository;

    @Autowired
    private CourseTypeRepository courseTypeRepository;

    @Autowired
    private MasterExamRepository masterExamRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            logger.info("Starting master data initialization...");
            
            // Initialize course types FIRST (required by subjects)
            initializeCourseTypes();
            
            // Initialize subjects
            initializeAcademicSubjects();
            initializeCompetitiveSubjects();
            
            // Initialize master exams
            initializeMasterExams();
            
            // Initialize master years
            initializeMasterYears();
            
            logger.info("Master data initialization completed successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize master data", e);
        }
    }

    /**
     * Initialize the 3 essential course types (Academic, Competitive, Professional)
     */
    private void initializeCourseTypes() {
        List<CourseTypeData> courseTypes = new ArrayList<>();
        
        // Define the 3 core course types with specific IDs
        courseTypes.add(new CourseTypeData("Academic", "Traditional school and college courses", StructureType.ACADEMIC, 1));
        courseTypes.add(new CourseTypeData("Competitive", "Competitive exam preparation courses", StructureType.COMPETITIVE, 2));
        courseTypes.add(new CourseTypeData("Professional", "Professional skill development courses", StructureType.PROFESSIONAL, 3));

        int inserted = 0;
        for (CourseTypeData data : courseTypes) {
            if (!courseTypeRepository.existsByNameIgnoreCase(data.name)) {
                CourseType courseType = new CourseType();
                courseType.setName(data.name);
                courseType.setDescription(data.description);
                courseType.setStructureType(data.structureType);
                courseType.setDisplayOrder(data.displayOrder);
                courseType.setIsActive(true);
                
                courseTypeRepository.save(courseType);
                inserted++;
                logger.info("Inserted course type: {} ({})", data.name, data.structureType);
            } else {
                logger.debug("Course type already exists: {}", data.name);
            }
        }
        
        logger.info("Course types initialization complete. Inserted: {}/{}", inserted, courseTypes.size());
    }

    /**
     * Initialize 5 common academic subjects for course_type_id = 1 (Academic)
     */
    private void initializeAcademicSubjects() {
        Optional<CourseType> academicType = courseTypeRepository.findById(1L);
        
        if (!academicType.isPresent()) {
            logger.warn("Academic course type (ID: 1) not found. Skipping academic subjects initialization.");
            return;
        }

        CourseType courseType = academicType.get();
        List<SubjectData> academicSubjects = new ArrayList<>();
        
        // Define 5 academic subjects
        academicSubjects.add(new SubjectData("Mathematics", 
            "Core mathematical concepts including algebra, geometry, calculus and statistics", 1));
        academicSubjects.add(new SubjectData("Science", 
            "General science covering physics, chemistry, and biology fundamentals", 2));
        academicSubjects.add(new SubjectData("English", 
            "English language, literature, grammar, and communication skills", 3));
        academicSubjects.add(new SubjectData("Social Studies", 
            "History, geography, civics, and social sciences", 4));
        academicSubjects.add(new SubjectData("Computer Science", 
            "Programming, algorithms, data structures, and computer fundamentals", 5));

        int inserted = 0;
        for (SubjectData subjectData : academicSubjects) {
            if (!subjectRepository.existsByNameAndCourseType(subjectData.name, courseType)) {
                Subject subject = new Subject();
                subject.setName(subjectData.name);
                subject.setDescription(subjectData.description);
                subject.setCourseType(courseType);
                subject.setDisplayOrder(subjectData.displayOrder);
                subject.setIsActive(true);
                subject.setCreatedBy("SYSTEM");
                
                subjectRepository.save(subject);
                inserted++;
                logger.info("Inserted academic subject: {}", subjectData.name);
            } else {
                logger.debug("Academic subject already exists: {}", subjectData.name);
            }
        }
        
        logger.info("Academic subjects initialization complete. Inserted: {}/{}", inserted, academicSubjects.size());
    }

    /**
     * Initialize 5 common competitive exam subjects for course_type_id = 2 (Competitive)
     */
    private void initializeCompetitiveSubjects() {
        Optional<CourseType> competitiveType = courseTypeRepository.findById(2L);
        
        if (!competitiveType.isPresent()) {
            logger.warn("Competitive course type (ID: 2) not found. Skipping competitive subjects initialization.");
            return;
        }

        CourseType courseType = competitiveType.get();
        List<SubjectData> competitiveSubjects = new ArrayList<>();
        
        // Define 5 competitive exam subjects
        competitiveSubjects.add(new SubjectData("Quantitative Aptitude", 
            "Numerical ability, data interpretation, and mathematical reasoning", 1));
        competitiveSubjects.add(new SubjectData("Logical Reasoning", 
            "Analytical reasoning, logical deductions, and problem-solving skills", 2));
        competitiveSubjects.add(new SubjectData("Verbal Ability", 
            "English comprehension, vocabulary, grammar, and verbal reasoning", 3));
        competitiveSubjects.add(new SubjectData("General Knowledge", 
            "Current affairs, general awareness, and static GK", 4));
        competitiveSubjects.add(new SubjectData("Technical Aptitude", 
            "Domain-specific technical knowledge and reasoning", 5));

        int inserted = 0;
        for (SubjectData subjectData : competitiveSubjects) {
            if (!subjectRepository.existsByNameAndCourseType(subjectData.name, courseType)) {
                Subject subject = new Subject();
                subject.setName(subjectData.name);
                subject.setDescription(subjectData.description);
                subject.setCourseType(courseType);
                subject.setDisplayOrder(subjectData.displayOrder);
                subject.setIsActive(true);
                subject.setCreatedBy("SYSTEM");
                
                subjectRepository.save(subject);
                inserted++;
                logger.info("Inserted competitive subject: {}", subjectData.name);
            } else {
                logger.debug("Competitive subject already exists: {}", subjectData.name);
            }
        }
        
        logger.info("Competitive subjects initialization complete. Inserted: {}/{}", inserted, competitiveSubjects.size());
    }

    /**
     * Initialize popular competitive exams (Master Exams)
     */
    private void initializeMasterExams() {
        List<ExamData> popularExams = new ArrayList<>();
        
        // Define popular competitive exams in India
        popularExams.add(new ExamData("JEE Main", 
            "Joint Entrance Examination (Main) for engineering admissions"));
        popularExams.add(new ExamData("JEE Advanced", 
            "Joint Entrance Examination (Advanced) for IIT admissions"));
        popularExams.add(new ExamData("NEET", 
            "National Eligibility cum Entrance Test for medical admissions"));
        popularExams.add(new ExamData("UPSC CSE", 
            "Union Public Service Commission Civil Services Examination"));
        popularExams.add(new ExamData("SSC CGL", 
            "Staff Selection Commission Combined Graduate Level Examination"));
        popularExams.add(new ExamData("GATE", 
            "Graduate Aptitude Test in Engineering"));
        popularExams.add(new ExamData("CAT", 
            "Common Admission Test for MBA programs"));
        popularExams.add(new ExamData("CLAT", 
            "Common Law Admission Test for law programs"));
        popularExams.add(new ExamData("NDA", 
            "National Defence Academy entrance examination"));
        popularExams.add(new ExamData("IBPS PO", 
            "Institute of Banking Personnel Selection - Probationary Officer"));

        int inserted = 0;
        for (ExamData examData : popularExams) {
            if (!masterExamRepository.existsByExamNameAndIsActiveTrue(examData.examName)) {
                MasterExam masterExam = new MasterExam();
                masterExam.setExamName(examData.examName);
                masterExam.setDescription(examData.description);
                masterExam.setIsActive(true);
                // Note: createdBy is a User entity, so we'll leave it null for system-generated data
                
                masterExamRepository.save(masterExam);
                inserted++;
                logger.info("Inserted master exam: {}", examData.examName);
            } else {
                logger.debug("Master exam already exists: {}", examData.examName);
            }
        }
        
        logger.info("Master exams initialization complete. Inserted: {}/{}", inserted, popularExams.size());
    }

    /**
     * Initialize master years from 2020 to 2030
     */
    private void initializeMasterYears() {
        int startYear = 2020;
        int endYear = 2030;
        int inserted = 0;

        for (int year = startYear; year <= endYear; year++) {
            Optional<MasterYear> existingYear = masterYearRepository.findByYearValue(year);
            
            if (!existingYear.isPresent()) {
                MasterYear masterYear = new MasterYear();
                masterYear.setYearValue(year);
                masterYear.setIsActive(true);
                
                masterYearRepository.save(masterYear);
                inserted++;
                logger.info("Inserted master year: {}", year);
            } else {
                logger.debug("Master year already exists: {}", year);
            }
        }
        
        logger.info("Master years initialization complete. Inserted: {}/{}", inserted, (endYear - startYear + 1));
    }

    /**
     * Inner class to hold subject data during initialization
     */
    private static class SubjectData {
        String name;
        String description;
        int displayOrder;

        SubjectData(String name, String description, int displayOrder) {
            this.name = name;
            this.description = description;
            this.displayOrder = displayOrder;
        }
    }

    /**
     * Inner class to hold exam data during initialization
     */
    private static class ExamData {
        String examName;
        String description;

        ExamData(String examName, String description) {
            this.examName = examName;
            this.description = description;
        }
    }

    /**
     * Inner class to hold course type data during initialization
     */
    private static class CourseTypeData {
        String name;
        String description;
        StructureType structureType;
        int displayOrder;

        CourseTypeData(String name, String description, StructureType structureType, int displayOrder) {
            this.name = name;
            this.description = description;
            this.structureType = structureType;
            this.displayOrder = displayOrder;
        }
    }
}

