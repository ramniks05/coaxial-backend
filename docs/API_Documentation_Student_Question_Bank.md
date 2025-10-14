# Student Question Bank API Documentation

## Table of Contents
1. [Overview](#overview)
2. [Authentication](#authentication)
3. [API Endpoints](#api-endpoints)
4. [Filter Options](#filter-options)
5. [Subscription Logic](#subscription-logic)
6. [Frontend Implementation](#frontend-implementation)
7. [Error Handling](#error-handling)
8. [Complete Examples](#complete-examples)

---

## Overview

The Student Question Bank module allows students to browse and practice questions from their subscribed courses. Questions include correct answers and explanations for learning purposes.

**Base URL:** `http://localhost:8080`

**API Base:** `/api/student/questions`

---

## Authentication

All endpoints require JWT authentication with `STUDENT` role.

**Required Header:**
```
Authorization: Bearer <JWT_TOKEN>
```

---

## API Endpoints

### 1. Get Accessible Questions (with Filters)

```
GET /api/student/questions
```

**Description:** Returns paginated list of questions the student can access based on their active subscriptions.

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `subjectId` | Long | No | - | Filter by subject ID |
| `topicId` | Long | No | - | Filter by topic ID |
| `moduleId` | Long | No | - | Filter by module ID |
| `chapterId` | Long | No | - | Filter by chapter ID |
| `questionType` | String | No | - | MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK, ESSAY |
| `difficultyLevel` | String | No | - | EASY, MEDIUM, HARD |
| `page` | Integer | No | 0 | Page number (0-indexed) |
| `size` | Integer | No | 20 | Page size (max: 100) |
| `sortBy` | String | No | id | Field to sort by |
| `sortDir` | String | No | asc | Sort direction (asc/desc) |

---

#### Example Requests

##### Get All Accessible Questions (First Page)
```bash
GET /api/student/questions?page=0&size=20
```

##### Get Questions by Chapter
```bash
GET /api/student/questions?chapterId=25&page=0&size=20
```

##### Get Medium Difficulty Questions
```bash
GET /api/student/questions?difficultyLevel=MEDIUM&page=0&size=10
```

##### Get Questions by Subject and Topic
```bash
GET /api/student/questions?subjectId=2&topicId=15&page=0&size=20
```

##### Get Multiple Choice Questions Only
```bash
GET /api/student/questions?questionType=MULTIPLE_CHOICE&page=0&size=20
```

##### Combined Filters
```bash
GET /api/student/questions?chapterId=25&difficultyLevel=HARD&questionType=MULTIPLE_CHOICE&page=0&size=10&sortBy=marks&sortDir=desc
```

---

#### Success Response (200 OK)

```json
{
  "content": [
    {
      "id": 1,
      "questionText": "What is the capital of France?",
      "questionType": "MULTIPLE_CHOICE",
      "difficultyLevel": "EASY",
      "marks": 1,
      "explanation": "Paris is the capital and largest city of France.",
      "chapterId": 25,
      "chapterName": "European Geography",
      "moduleId": 10,
      "moduleName": "Western Europe",
      "topicId": 5,
      "topicName": "World Geography",
      "subjectId": 2,
      "subjectName": "Geography",
      "options": [
        {
          "optionId": 1,
          "optionText": "London",
          "isCorrect": false
        },
        {
          "optionId": 2,
          "optionText": "Paris",
          "isCorrect": true
        },
        {
          "optionId": 3,
          "optionText": "Berlin",
          "isCorrect": false
        },
        {
          "optionId": 4,
          "optionText": "Madrid",
          "isCorrect": false
        }
      ]
    },
    {
      "id": 2,
      "questionText": "Solve: 2x + 5 = 15. Find x.",
      "questionType": "MULTIPLE_CHOICE",
      "difficultyLevel": "MEDIUM",
      "marks": 2,
      "explanation": "Subtract 5 from both sides: 2x = 10. Divide by 2: x = 5.",
      "chapterId": 30,
      "chapterName": "Linear Equations",
      "moduleId": 12,
      "moduleName": "Algebra Basics",
      "topicId": 6,
      "topicName": "Mathematics",
      "subjectId": 1,
      "subjectName": "Mathematics",
      "options": [
        {
          "optionId": 5,
          "optionText": "3",
          "isCorrect": false
        },
        {
          "optionId": 6,
          "optionText": "5",
          "isCorrect": true
        },
        {
          "optionId": 7,
          "optionText": "7",
          "isCorrect": false
        },
        {
          "optionId": 8,
          "optionText": "10",
          "isCorrect": false
        }
      ]
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 8,
  "totalElements": 150,
  "last": false,
  "first": true,
  "numberOfElements": 20,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "empty": false
}
```

---

#### Error Responses

##### No Active Subscription (200 OK - Empty Result)
```json
{
  "content": [],
  "totalElements": 0,
  "totalPages": 0,
  "empty": true
}
```

##### Unauthorized (401)
```json
{
  "error": "Unauthorized",
  "message": "Full authentication is required"
}
```

##### Server Error (500)
```json
{
  "error": "Failed to fetch questions"
}
```

---

### 2. Get Question by ID

```
GET /api/student/questions/{id}
```

**Description:** Retrieves a specific question with full details including correct answers and explanation.

**Path Parameter:**
- `id` - Question ID

---

#### Example Request

```bash
GET /api/student/questions/1
```

---

#### Success Response (200 OK)

```json
{
  "id": 1,
  "questionText": "What is the capital of France?",
  "questionType": "MULTIPLE_CHOICE",
  "difficultyLevel": "EASY",
  "marks": 1,
  "explanation": "Paris is the capital and largest city of France, established in the 3rd century BC.",
  "chapterId": 25,
  "chapterName": "European Geography",
  "moduleId": 10,
  "moduleName": "Western Europe",
  "topicId": 5,
  "topicName": "World Geography",
  "subjectId": 2,
  "subjectName": "Geography",
  "options": [
    {
      "optionId": 1,
      "optionText": "London",
      "isCorrect": false
    },
    {
      "optionId": 2,
      "optionText": "Paris",
      "isCorrect": true
    },
    {
      "optionId": 3,
      "optionText": "Berlin",
      "isCorrect": false
    },
    {
      "optionId": 4,
      "optionText": "Madrid",
      "isCorrect": false
    }
  ]
}
```

---

#### Error Responses

##### No Access (400 Bad Request)
```json
{
  "error": "You do not have access to this question. Please purchase a subscription."
}
```

##### Question Not Found (400 Bad Request)
```json
{
  "error": "Question not found"
}
```

---

## Filter Options

### Available Filters

#### 1. Hierarchy Filters
- **subjectId** - Filter questions by subject
- **topicId** - Filter questions by topic
- **moduleId** - Filter questions by module
- **chapterId** - Filter questions by chapter (most specific)

#### 2. Question Type Filters
- `MULTIPLE_CHOICE` - MCQ questions
- `TRUE_FALSE` - True/False questions
- `FILL_BLANK` - Fill in the blank
- `ESSAY` - Descriptive answers

#### 3. Difficulty Level Filters
- `EASY` - Easy questions
- `MEDIUM` - Medium difficulty
- `HARD` - Hard questions

#### 4. Pagination & Sorting
- `page` - Page number (starts from 0)
- `size` - Number of items per page (1-100)
- `sortBy` - Field to sort by (id, marks, difficultyLevel)
- `sortDir` - Sort direction (asc, desc)

---

### Filter Combinations

#### Example 1: Get Easy Questions from Specific Chapter
```
GET /api/student/questions?chapterId=25&difficultyLevel=EASY&page=0&size=10
```

#### Example 2: Get All Multiple Choice Questions by Topic
```
GET /api/student/questions?topicId=5&questionType=MULTIPLE_CHOICE&page=0&size=20
```

#### Example 3: Get Hard Questions, Sorted by Marks
```
GET /api/student/questions?difficultyLevel=HARD&sortBy=marks&sortDir=desc&page=0&size=15
```

---

## Subscription Logic

### How Questions are Filtered by Subscription

#### Student with CLASS Subscription
```
Student has: Class 10 subscription
Can access: All questions from Class 10 subjects, topics, modules, chapters
```

#### Student with EXAM Subscription
```
Student has: JEE Main subscription
Can access: All questions from JEE Main subjects, topics, modules, chapters
```

#### Student with COURSE Subscription
```
Student has: Mathematics Course subscription
Can access: All questions from all classes/exams in Mathematics course
```

#### Student with Multiple Subscriptions
```
Student has:
- Class 10 subscription
- JEE Main subscription

Can access: Questions from BOTH Class 10 AND JEE Main
Result: Union of all accessible questions
```

---

### Access Validation Flow

```
1. Student requests questions
   ↓
2. Backend fetches student's active subscriptions
   ↓
3. For each subscription, determine accessible content:
   - COURSE subscription → All questions in that course
   - CLASS subscription → All questions in that class
   - EXAM subscription → All questions in that exam
   ↓
4. Apply additional filters (chapter, difficulty, type)
   ↓
5. Return paginated results
```

---

## Frontend Implementation

### Complete TypeScript/React Implementation

```typescript
// types/question.ts
export interface StudentQuestion {
  id: number;
  questionText: string;
  questionType: 'MULTIPLE_CHOICE' | 'TRUE_FALSE' | 'FILL_BLANK' | 'ESSAY';
  difficultyLevel: 'EASY' | 'MEDIUM' | 'HARD';
  marks: number;
  explanation: string;
  chapterId: number;
  chapterName: string;
  moduleId: number;
  moduleName: string;
  topicId: number;
  topicName: string;
  subjectId: number;
  subjectName: string;
  options: QuestionOption[];
}

export interface QuestionOption {
  optionId: number;
  optionText: string;
  isCorrect: boolean;
}

export interface QuestionFilters {
  subjectId?: number;
  topicId?: number;
  moduleId?: number;
  chapterId?: number;
  questionType?: string;
  difficultyLevel?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}

export interface PaginatedResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}

// services/questionService.ts
import axios from 'axios';

const API_BASE = 'http://localhost:8080';

export const questionService = {
  /**
   * Get accessible questions with filters
   */
  getQuestions: async (filters: QuestionFilters): Promise<PaginatedResponse<StudentQuestion>> => {
    const params = new URLSearchParams();
    
    if (filters.subjectId) params.append('subjectId', filters.subjectId.toString());
    if (filters.topicId) params.append('topicId', filters.topicId.toString());
    if (filters.moduleId) params.append('moduleId', filters.moduleId.toString());
    if (filters.chapterId) params.append('chapterId', filters.chapterId.toString());
    if (filters.questionType) params.append('questionType', filters.questionType);
    if (filters.difficultyLevel) params.append('difficultyLevel', filters.difficultyLevel);
    if (filters.page !== undefined) params.append('page', filters.page.toString());
    if (filters.size) params.append('size', filters.size.toString());
    if (filters.sortBy) params.append('sortBy', filters.sortBy);
    if (filters.sortDir) params.append('sortDir', filters.sortDir);
    
    const response = await axios.get(
      `${API_BASE}/api/student/questions?${params.toString()}`,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('studentToken')}`
        }
      }
    );
    
    return response.data;
  },

  /**
   * Get specific question by ID
   */
  getQuestionById: async (questionId: number): Promise<StudentQuestion> => {
    const response = await axios.get(
      `${API_BASE}/api/student/questions/${questionId}`,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('studentToken')}`
        }
      }
    );
    
    return response.data;
  }
};

// components/QuestionBank.tsx
import React, { useState, useEffect } from 'react';
import { questionService } from '../services/questionService';
import type { StudentQuestion, QuestionFilters, PaginatedResponse } from '../types/question';

export const QuestionBank: React.FC = () => {
  const [questions, setQuestions] = useState<PaginatedResponse<StudentQuestion> | null>(null);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState<QuestionFilters>({
    page: 0,
    size: 20,
    sortBy: 'id',
    sortDir: 'asc'
  });
  const [selectedQuestion, setSelectedQuestion] = useState<StudentQuestion | null>(null);

  // Load questions when filters change
  useEffect(() => {
    loadQuestions();
  }, [filters]);

  const loadQuestions = async () => {
    try {
      setLoading(true);
      const data = await questionService.getQuestions(filters);
      setQuestions(data);
    } catch (error: any) {
      console.error('Error loading questions:', error);
      if (error.response?.status === 401) {
        // Redirect to login
        window.location.href = '/login';
      }
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (key: keyof QuestionFilters, value: any) => {
    setFilters(prev => ({
      ...prev,
      [key]: value,
      page: 0 // Reset to first page when filter changes
    }));
  };

  const handlePageChange = (newPage: number) => {
    setFilters(prev => ({ ...prev, page: newPage }));
  };

  const viewQuestionDetails = async (questionId: number) => {
    try {
      const question = await questionService.getQuestionById(questionId);
      setSelectedQuestion(question);
    } catch (error) {
      console.error('Error loading question details:', error);
      alert('Failed to load question details');
    }
  };

  if (loading) {
    return <div className="loading">Loading questions...</div>;
  }

  if (!questions || questions.content.length === 0) {
    return (
      <div className="no-questions">
        <h2>No Questions Available</h2>
        <p>
          {filters.chapterId || filters.moduleId || filters.topicId || filters.subjectId
            ? 'No questions found with the selected filters.'
            : 'You do not have any active subscriptions. Please subscribe to a course to access questions.'}
        </p>
      </div>
    );
  }

  return (
    <div className="question-bank">
      {/* Filter Panel */}
      <div className="filters-panel">
        <h2>Question Bank</h2>
        
        <div className="filter-group">
          <label>Difficulty Level:</label>
          <select 
            value={filters.difficultyLevel || ''} 
            onChange={(e) => handleFilterChange('difficultyLevel', e.target.value || undefined)}
          >
            <option value="">All Levels</option>
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
          </select>
        </div>

        <div className="filter-group">
          <label>Question Type:</label>
          <select 
            value={filters.questionType || ''} 
            onChange={(e) => handleFilterChange('questionType', e.target.value || undefined)}
          >
            <option value="">All Types</option>
            <option value="MULTIPLE_CHOICE">Multiple Choice</option>
            <option value="TRUE_FALSE">True/False</option>
            <option value="FILL_BLANK">Fill in the Blank</option>
          </select>
        </div>

        <div className="filter-group">
          <label>Chapter ID:</label>
          <input 
            type="number" 
            value={filters.chapterId || ''} 
            onChange={(e) => handleFilterChange('chapterId', e.target.value ? Number(e.target.value) : undefined)}
            placeholder="Enter chapter ID"
          />
        </div>

        <button onClick={() => setFilters({ page: 0, size: 20 })}>
          Clear Filters
        </button>
      </div>

      {/* Questions List */}
      <div className="questions-list">
        <div className="results-info">
          <p>
            Showing {questions.content.length} of {questions.totalElements} questions
            (Page {questions.number + 1} of {questions.totalPages})
          </p>
        </div>

        {questions.content.map((question) => (
          <div key={question.id} className="question-card">
            <div className="question-header">
              <span className={`difficulty-badge ${question.difficultyLevel.toLowerCase()}`}>
                {question.difficultyLevel}
              </span>
              <span className="marks-badge">{question.marks} marks</span>
            </div>

            <h3 className="question-text">{question.questionText}</h3>

            <div className="question-meta">
              <span>📚 {question.subjectName}</span>
              <span>📖 {question.chapterName}</span>
              <span>📝 {question.questionType}</span>
            </div>

            <div className="options">
              {question.options.map((option) => (
                <div 
                  key={option.optionId} 
                  className={`option ${option.isCorrect ? 'correct' : ''}`}
                >
                  <span className="option-text">{option.optionText}</span>
                  {option.isCorrect && <span className="correct-badge">✓ Correct</span>}
                </div>
              ))}
            </div>

            {question.explanation && (
              <div className="explanation">
                <strong>💡 Explanation:</strong>
                <p>{question.explanation}</p>
              </div>
            )}

            <button 
              className="details-button"
              onClick={() => viewQuestionDetails(question.id)}
            >
              View Details
            </button>
          </div>
        ))}
      </div>

      {/* Pagination */}
      <div className="pagination">
        <button
          disabled={questions.first}
          onClick={() => handlePageChange(filters.page! - 1)}
        >
          Previous
        </button>

        <span className="page-info">
          Page {questions.number + 1} of {questions.totalPages}
        </span>

        <button
          disabled={questions.last}
          onClick={() => handlePageChange(filters.page! + 1)}
        >
          Next
        </button>

        <select
          value={filters.size}
          onChange={(e) => handleFilterChange('size', Number(e.target.value))}
        >
          <option value={10}>10 per page</option>
          <option value={20}>20 per page</option>
          <option value={50}>50 per page</option>
          <option value={100}>100 per page</option>
        </select>
      </div>

      {/* Question Details Modal */}
      {selectedQuestion && (
        <div className="modal-overlay" onClick={() => setSelectedQuestion(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <button className="close-button" onClick={() => setSelectedQuestion(null)}>×</button>
            
            <h2>Question Details</h2>
            
            <div className="question-details">
              <div className="detail-section">
                <h3>{selectedQuestion.questionText}</h3>
                
                <div className="metadata">
                  <p><strong>Type:</strong> {selectedQuestion.questionType}</p>
                  <p><strong>Difficulty:</strong> {selectedQuestion.difficultyLevel}</p>
                  <p><strong>Marks:</strong> {selectedQuestion.marks}</p>
                  <p><strong>Subject:</strong> {selectedQuestion.subjectName}</p>
                  <p><strong>Topic:</strong> {selectedQuestion.topicName}</p>
                  <p><strong>Module:</strong> {selectedQuestion.moduleName}</p>
                  <p><strong>Chapter:</strong> {selectedQuestion.chapterName}</p>
                </div>
              </div>

              <div className="options-section">
                <h4>Options:</h4>
                {selectedQuestion.options.map((option, index) => (
                  <div 
                    key={option.optionId}
                    className={`option-detail ${option.isCorrect ? 'correct-answer' : ''}`}
                  >
                    <span className="option-letter">{String.fromCharCode(65 + index)}.</span>
                    <span className="option-text">{option.optionText}</span>
                    {option.isCorrect && <span className="badge">✓ Correct Answer</span>}
                  </div>
                ))}
              </div>

              {selectedQuestion.explanation && (
                <div className="explanation-section">
                  <h4>💡 Explanation:</h4>
                  <p>{selectedQuestion.explanation}</p>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// styles/QuestionBank.css
const styles = `
.question-bank {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.filters-panel {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.filter-group {
  margin-bottom: 15px;
}

.filter-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.filter-group select,
.filter-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.question-card {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.difficulty-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.difficulty-badge.easy {
  background: #4caf50;
  color: white;
}

.difficulty-badge.medium {
  background: #ff9800;
  color: white;
}

.difficulty-badge.hard {
  background: #f44336;
  color: white;
}

.marks-badge {
  padding: 4px 12px;
  background: #2196f3;
  color: white;
  border-radius: 12px;
  font-size: 12px;
}

.question-text {
  font-size: 18px;
  margin: 15px 0;
  color: #333;
}

.options {
  margin: 15px 0;
}

.option {
  padding: 12px;
  margin: 8px 0;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  background: #fafafa;
}

.option.correct {
  border-color: #4caf50;
  background: #e8f5e9;
}

.correct-badge {
  float: right;
  color: #4caf50;
  font-weight: bold;
}

.explanation {
  margin-top: 15px;
  padding: 15px;
  background: #fff3e0;
  border-left: 4px solid #ff9800;
  border-radius: 4px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 30px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination button:hover:not(:disabled) {
  background: #f5f5f5;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 12px;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 24px;
  border: none;
  background: none;
  cursor: pointer;
}
`;
```

---

## Complete User Flow

### Scenario 1: Browse Questions by Chapter

```
1. Student navigates to Question Bank
   ↓
2. Selects Chapter from dropdown
   ↓
3. Frontend calls:
   GET /api/student/questions?chapterId=25&page=0&size=20
   ↓
4. Backend validates subscription
   ↓
5. Returns questions from that chapter
   ↓
6. Student sees questions with correct answers highlighted
   ↓
7. Student can read explanations for learning
```

---

### Scenario 2: Filter by Difficulty

```
1. Student selects "Hard" difficulty
   ↓
2. Frontend calls:
   GET /api/student/questions?difficultyLevel=HARD&page=0&size=20
   ↓
3. Returns only hard questions from subscribed content
   ↓
4. Student practices challenging questions
```

---

### Scenario 3: Pagination

```
1. Student on page 1 (20 questions shown)
   ↓
2. Clicks "Next"
   ↓
3. Frontend calls:
   GET /api/student/questions?page=1&size=20
   ↓
4. Returns next 20 questions
```

---

## Error Handling

### Common Errors and Solutions

#### 1. No Subscription
**Error:** Empty result (200 OK with empty content array)
```json
{
  "content": [],
  "totalElements": 0
}
```

**Frontend Action:**
```javascript
if (data.totalElements === 0 && !hasAnyFilters) {
  showMessage('No questions available. Please subscribe to a course.');
  redirectTo('/subscriptions');
}
```

---

#### 2. No Access to Specific Question
**Error:** 400 Bad Request
```json
{
  "error": "You do not have access to this question. Please purchase a subscription."
}
```

**Frontend Action:**
```javascript
catch (error) {
  if (error.response?.status === 400) {
    alert('You need a subscription to access this question.');
  }
}
```

---

#### 3. Invalid Page Number
**Error:** Returns empty page (200 OK)
```json
{
  "content": [],
  "totalPages": 5,
  "number": 10
}
```

**Frontend Action:**
```javascript
if (data.number >= data.totalPages && data.totalPages > 0) {
  // Reset to first page
  setFilters(prev => ({ ...prev, page: 0 }));
}
```

---

## Advanced Features

### 1. Search Functionality (Future Enhancement)

Add text search to filter:
```
GET /api/student/questions?search=algebra&page=0&size=20
```

### 2. Bookmark Questions (Future Enhancement)

```
POST /api/student/questions/{id}/bookmark
DELETE /api/student/questions/{id}/bookmark
GET /api/student/questions/bookmarked
```

### 3. Question Practice History (Future Enhancement)

Track which questions student has practiced:
```
GET /api/student/questions/practiced
POST /api/student/questions/{id}/mark-practiced
```

---

## Testing Guide

### Manual Testing Checklist

- [ ] **No Subscription:**
  - [ ] Returns empty array
  - [ ] Shows appropriate message
  
- [ ] **With CLASS Subscription:**
  - [ ] Shows questions from that class
  - [ ] Doesn't show questions from other classes
  
- [ ] **With EXAM Subscription:**
  - [ ] Shows questions from that exam
  - [ ] Doesn't show questions from other exams
  
- [ ] **Filters:**
  - [ ] Chapter filter works
  - [ ] Difficulty filter works
  - [ ] Question type filter works
  - [ ] Combined filters work
  
- [ ] **Pagination:**
  - [ ] First page loads
  - [ ] Next/Previous buttons work
  - [ ] Page size change works
  - [ ] Last page shows correctly
  
- [ ] **Question Display:**
  - [ ] Correct answer is highlighted
  - [ ] Explanation is visible
  - [ ] All options display
  - [ ] Hierarchy info shows (chapter, module, topic, subject)
  
- [ ] **Security:**
  - [ ] Requires authentication
  - [ ] Validates subscription
  - [ ] Can't access unsubscribed content

---

## cURL Examples

### Get All Accessible Questions
```bash
curl -X GET "http://localhost:8080/api/student/questions?page=0&size=20" \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

### Get Questions by Chapter
```bash
curl -X GET "http://localhost:8080/api/student/questions?chapterId=25&page=0&size=10" \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

### Get Easy Questions Only
```bash
curl -X GET "http://localhost:8080/api/student/questions?difficultyLevel=EASY&page=0&size=20" \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

### Get Specific Question
```bash
curl -X GET "http://localhost:8080/api/student/questions/1" \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

### Get Multiple Choice Questions, Sorted by Marks
```bash
curl -X GET "http://localhost:8080/api/student/questions?questionType=MULTIPLE_CHOICE&sortBy=marks&sortDir=desc&page=0&size=20" \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

## Postman Collection

### Collection: Student Question Bank

#### 1. Get Questions - No Filters
- **Method:** GET
- **URL:** `{{baseUrl}}/api/student/questions?page=0&size=20`
- **Headers:** `Authorization: Bearer {{studentToken}}`

#### 2. Get Questions - Filter by Chapter
- **Method:** GET
- **URL:** `{{baseUrl}}/api/student/questions?chapterId=25&page=0&size=20`
- **Headers:** `Authorization: Bearer {{studentToken}}`

#### 3. Get Questions - Filter by Difficulty
- **Method:** GET
- **URL:** `{{baseUrl}}/api/student/questions?difficultyLevel=HARD&page=0&size=10`
- **Headers:** `Authorization: Bearer {{studentToken}}`

#### 4. Get Question by ID
- **Method:** GET
- **URL:** `{{baseUrl}}/api/student/questions/1`
- **Headers:** `Authorization: Bearer {{studentToken}}`

---

## Database Query Performance

### Optimization Tips

1. **Add Indexes:**
```sql
CREATE INDEX idx_questions_subject ON questions(subject_id);
CREATE INDEX idx_questions_topic ON questions(topic_id);
CREATE INDEX idx_questions_module ON questions(module_id);
CREATE INDEX idx_questions_chapter ON questions(chapter_id);
CREATE INDEX idx_questions_difficulty ON questions(difficulty_level);
CREATE INDEX idx_questions_type ON questions(question_type);
CREATE INDEX idx_questions_active ON questions(is_active);
```

2. **Pagination:**
- Always use pagination to avoid loading too many questions
- Default page size: 20
- Maximum page size: 100

3. **Filter Early:**
- Apply subscription filter first
- Then apply user-selected filters
- Minimize database queries

---

## API Response Times (Expected)

| Endpoint | Expected Time | Notes |
|----------|---------------|-------|
| GET /questions (no filter) | < 500ms | With active subscriptions |
| GET /questions (with filters) | < 300ms | Filtered query is faster |
| GET /questions/{id} | < 100ms | Single record fetch |

---

## Security Considerations

### 1. Correct Answer Visibility
- ✅ Correct answers are visible (for learning/practice mode)
- ✅ This is intentional - students can learn from answers
- ✅ For testing, use the Test Execution API (hides correct answers)

### 2. Subscription Validation
- ✅ Validates on every request
- ✅ Checks payment status (PAID)
- ✅ Checks expiry date
- ✅ Checks active status

### 3. Data Security
- ✅ No sensitive data exposed
- ✅ Only shows questions student has paid for
- ✅ JWT token required for all requests

---

## Integration with Test Management

### Relationship

```
Question Bank (Practice Mode)          Test Execution (Exam Mode)
└─ Shows correct answers         vs    └─ Hides correct answers
└─ Shows explanations                  └─ Shows after submission
└─ No time limit                       └─ Time-limited
└─ Browse freely                       └─ Session-based
└─ For learning                        └─ For assessment
```

### Combined Usage

```javascript
// Student flow:
1. Browse Question Bank → Learn concepts
2. Practice questions → See correct answers
3. Ready to test → Start Test → Take exam
4. Submit test → See results
5. Review incorrect answers → Back to Question Bank
```

---

## Frequently Asked Questions

### Q1: Why do students see correct answers in question bank?
**A:** This is a learning/practice feature. Students can study questions and learn from explanations. For actual testing, use the Test Execution API which hides correct answers.

### Q2: Can students access questions without subscription?
**A:** No. All questions are filtered by active subscriptions. Students must have at least one active COURSE/CLASS/EXAM subscription.

### Q3: How does pagination work?
**A:** Standard Spring Boot pagination:
- `page=0` is first page
- `size=20` means 20 items per page
- Use `totalPages` and `totalElements` for UI

### Q4: Can filters be combined?
**A:** Yes! All filters can be combined:
```
?chapterId=25&difficultyLevel=HARD&questionType=MULTIPLE_CHOICE&page=0&size=10
```

### Q5: What if a subscription expires?
**A:** Questions from that subscription will no longer appear in results. The API automatically filters by active subscriptions.

---

## Support & Updates

**API Version:** 1.0  
**Last Updated:** October 14, 2025  
**Backend Team:** Coaxial Development Team

For issues or feature requests, contact the backend team.

---

## Summary

### Endpoints
- ✅ `GET /api/student/questions` - Get questions with filters
- ✅ `GET /api/student/questions/{id}` - Get specific question

### Features
- ✅ Subscription-based access control
- ✅ Multiple filter options
- ✅ Pagination support
- ✅ Shows correct answers for learning
- ✅ Shows explanations
- ✅ Hierarchical filtering (subject → topic → module → chapter)

### Security
- ✅ JWT authentication required
- ✅ STUDENT role required
- ✅ Subscription validation on every request
- ✅ Auto-filters by active subscriptions

**The Student Question Bank API is production-ready!** 🚀

