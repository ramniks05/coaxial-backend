package com.coaxial.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TestQuestionRequestDTO {
    @NotNull
    private Long questionId;

    @Min(value = 1, message = "Question order must be at least 1")
    private Integer questionOrder = 1;

    @Min(value = 0, message = "Marks cannot be negative")
    private Integer marks = 1;

    private Double negativeMarks = 0.0;

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Integer getQuestionOrder() { return questionOrder; }
    public void setQuestionOrder(Integer questionOrder) { this.questionOrder = questionOrder; }

    public Integer getMarks() { return marks; }
    public void setMarks(Integer marks) { this.marks = marks; }

    public Double getNegativeMarks() { return negativeMarks; }
    public void setNegativeMarks(Double negativeMarks) { this.negativeMarks = negativeMarks; }
}


