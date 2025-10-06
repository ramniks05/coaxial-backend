package com.coaxial.dto;

import jakarta.validation.constraints.NotNull;

public class QuestionExamHistoryRequestDTO {
    
    @NotNull(message = "Master exam ID is required")
    private Long masterExamId;
    
    @NotNull(message = "Appeared year is required")
    private Integer appearedYear; // Backward compatibility: accept yearId or actual year
    
    // New: explicit year master id if frontend sends yearId directly
    private Long appearedYearId;
    
    private String appearedSession;
    private Integer marksInExam;
    private String questionNumberInExam;
    private String difficultyInExam;
    private String notes;
    
    // Constructors
    public QuestionExamHistoryRequestDTO() {}
    
    public QuestionExamHistoryRequestDTO(Long masterExamId, Integer appearedYear) {
        this.masterExamId = masterExamId;
        this.appearedYear = appearedYear;
    }
    
    // Getters and Setters
    public Long getMasterExamId() { return masterExamId; }
    public void setMasterExamId(Long masterExamId) { this.masterExamId = masterExamId; }
    
    public Integer getAppearedYear() { return appearedYear; }
    public void setAppearedYear(Integer appearedYear) { this.appearedYear = appearedYear; }
    
    public Long getAppearedYearId() { return appearedYearId; }
    public void setAppearedYearId(Long appearedYearId) { this.appearedYearId = appearedYearId; }
    
    public String getAppearedSession() { return appearedSession; }
    public void setAppearedSession(String appearedSession) { this.appearedSession = appearedSession; }
    
    public Integer getMarksInExam() { return marksInExam; }
    public void setMarksInExam(Integer marksInExam) { this.marksInExam = marksInExam; }
    
    public String getQuestionNumberInExam() { return questionNumberInExam; }
    public void setQuestionNumberInExam(String questionNumberInExam) { this.questionNumberInExam = questionNumberInExam; }
    
    public String getDifficultyInExam() { return difficultyInExam; }
    public void setDifficultyInExam(String difficultyInExam) { this.difficultyInExam = difficultyInExam; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
