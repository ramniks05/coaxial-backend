package com.coaxial.entity;

public enum ProfessionalSubject {
    // Technology
    SOFTWARE_DEVELOPMENT("Software Development", "Programming and software engineering"),
    WEB_DEVELOPMENT("Web Development", "Frontend and backend web technologies"),
    MOBILE_DEVELOPMENT("Mobile Development", "Mobile app development"),
    DATA_SCIENCE("Data Science", "Data analysis and machine learning"),
    CYBERSECURITY("Cybersecurity", "Information security and protection"),
    CLOUD_COMPUTING("Cloud Computing", "Cloud platforms and services"),
    DEVOPS("DevOps", "Development and operations practices"),
    
    // Business
    PROJECT_MANAGEMENT("Project Management", "Project planning and execution"),
    BUSINESS_ANALYSIS("Business Analysis", "Business process analysis and improvement"),
    DIGITAL_MARKETING("Digital Marketing", "Online marketing strategies and tools"),
    SALES_MANAGEMENT("Sales Management", "Sales techniques and customer relations"),
    HUMAN_RESOURCES("Human Resources", "HR management and practices"),
    FINANCIAL_MANAGEMENT("Financial Management", "Corporate finance and investment"),
    
    // Design
    GRAPHIC_DESIGN("Graphic Design", "Visual design and branding"),
    UI_UX_DESIGN("UI/UX Design", "User interface and experience design"),
    WEB_DESIGN("Web Design", "Website design and layout"),
    
    // Healthcare
    NURSING("Nursing", "Patient care and medical assistance"),
    PHARMACY("Pharmacy", "Pharmaceutical sciences and drug management"),
    MEDICAL_CODING("Medical Coding", "Medical billing and coding"),
    
    // Finance
    ACCOUNTING("Accounting", "Financial accounting and bookkeeping"),
    TAXATION("Taxation", "Tax laws and compliance"),
    AUDITING("Auditing", "Financial auditing and compliance"),
    INVESTMENT_ANALYSIS("Investment Analysis", "Investment strategies and portfolio management"),
    
    // Language and Communication
    TECHNICAL_WRITING("Technical Writing", "Documentation and technical communication"),
    CONTENT_WRITING("Content Writing", "Content creation and copywriting"),
    TRANSLATION("Translation", "Language translation services"),
    
    // Other Professional Skills
    LEADERSHIP("Leadership", "Leadership skills and team management"),
    COMMUNICATION_SKILLS("Communication Skills", "Professional communication and presentation"),
    TIME_MANAGEMENT("Time Management", "Productivity and time optimization"),
    CRITICAL_THINKING("Critical Thinking", "Analytical thinking and problem solving");
    
    private final String displayName;
    private final String description;
    
    ProfessionalSubject(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
}
