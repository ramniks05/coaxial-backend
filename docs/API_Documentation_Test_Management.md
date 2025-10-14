# Test Management API Documentation for Frontend

## Table of Contents
1. [Overview](#overview)
2. [Authentication](#authentication)
3. [API Endpoints](#api-endpoints)
4. [Complete Test Flow](#complete-test-flow)
5. [Error Handling](#error-handling)
6. [Example Implementation](#example-implementation)

---

## Overview

This document provides complete API documentation for implementing the test management system on the frontend. The system supports subscription-based test access, test execution, answer submission, and result viewing.

**Base URL:** `http://localhost:8080`

**Student API Base:** `/api/student`

---

## Authentication

All endpoints require JWT authentication with `STUDENT` role.

**Header Required:**
```
Authorization: Bearer <JWT_TOKEN>
```

---

## API Endpoints

### 1. Get Available Tests (Subscription-Based)

#### Get All Accessible Tests
```
GET /api/student/dashboard/tests
```

**Description:** Returns all tests the student can access based on their active subscriptions.

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
[
  {
    "id": 1,
    "testName": "Class 10 - Algebra Test",
    "description": "Basic algebra concepts test",
    "timeLimitMinutes": 60,
    "totalMarks": 100,
    "passingMarks": 40,
    "testCreationMode": "CONTENT_BASED",
    "testLevel": "CHAPTER",
    "isPublished": true,
    "courseId": 1,
    "courseName": "Mathematics",
    "classId": 10,
    "className": "Class 10",
    "negativeMarking": true,
    "negativeMarkPercentage": 0.25,
    "maxAttempts": 3
  }
]
```

---

#### Get Tests by Class
```
GET /api/student/dashboard/tests?classId={classId}
```

**Use Case:** When student clicks on their subscribed class

**Example:**
```
GET /api/student/dashboard/tests?classId=10
```

---

#### Get Tests by Exam
```
GET /api/student/dashboard/tests?examId={examId}
```

**Use Case:** When student clicks on their subscribed exam

**Example:**
```
GET /api/student/dashboard/tests?examId=5
```

---

#### Get Tests by Course
```
GET /api/student/dashboard/tests?courseId={courseId}
```

**Example:**
```
GET /api/student/dashboard/tests?courseId=1
```

---

### 2. Check Active Session (Before Starting Test)

```
GET /api/student/tests/{testId}/active-session
```

**Description:** Check if student already has an active session for this test.

**Response (No Active Session):**
```json
{
  "hasActiveSession": false
}
```

**Response (Active Session Exists):**
```json
{
  "sessionId": "abc-123-uuid-456",
  "attemptId": 15,
  "testId": 2,
  "testName": "Class 10 Algebra Test",
  "timeLimitMinutes": 60,
  "totalQuestions": 50,
  "attemptNumber": 1,
  "startedAt": "2025-10-14T10:00:00",
  "expiresAt": "2025-10-14T11:00:00",
  "status": "IN_PROGRESS",
  "negativeMarking": true,
  "negativeMarkPercentage": 0.25,
  "allowReview": true,
  "showCorrectAnswers": false,
  "allowSkip": true
}
```

---

### 3. Start Test

```
POST /api/student/tests/{testId}/start
```

**Description:** Starts a new test session and attempt. Validates subscription access and max attempts.

**Request Body:**
```json
{
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
}
```

**Success Response (200):**
```json
{
  "sessionId": "abc-123-uuid-456",
  "attemptId": 1,
  "testId": 10,
  "testName": "Class 10 - Algebra Test",
  "timeLimitMinutes": 60,
  "totalQuestions": 50,
  "attemptNumber": 1,
  "startedAt": "2025-10-14T10:00:00",
  "expiresAt": "2025-10-14T11:00:00",
  "status": "STARTED",
  "negativeMarking": true,
  "negativeMarkPercentage": 0.25,
  "allowReview": true,
  "showCorrectAnswers": false,
  "allowSkip": true
}
```

**Error Responses:**

**400 - Already has active session:**
```json
{
  "error": "You already have an active session for this test. Please continue or submit the existing attempt."
}
```

**400 - Max attempts reached:**
```json
{
  "error": "You have reached the maximum number of attempts (3) for this test"
}
```

**400 - No subscription:**
```json
{
  "error": "You do not have access to this test. Please purchase a subscription."
}
```

---

### 4. Get Test Questions

```
GET /api/student/tests/{testId}/questions?sessionId={sessionId}
```

**Description:** Retrieves all questions for the active test session. Correct answers are hidden.

**Example:**
```
GET /api/student/tests/2/questions?sessionId=abc-123-uuid-456
```

**Response:**
```json
[
  {
    "questionId": 1,
    "questionText": "What is 2 + 2?",
    "questionType": "MULTIPLE_CHOICE",
    "questionOrder": 1,
    "marks": 4.0,
    "imageUrl": null,
    "options": [
      {
        "optionId": 1,
        "optionText": "3",
        "optionImageUrl": null
      },
      {
        "optionId": 2,
        "optionText": "4",
        "optionImageUrl": null
      },
      {
        "optionId": 3,
        "optionText": "5",
        "optionImageUrl": null
      },
      {
        "optionId": 4,
        "optionText": "6",
        "optionImageUrl": null
      }
    ]
  },
  {
    "questionId": 2,
    "questionText": "Solve: x + 5 = 10",
    "questionType": "MULTIPLE_CHOICE",
    "questionOrder": 2,
    "marks": 4.0,
    "options": [...]
  }
]
```

---

### 5. Submit Answer

```
POST /api/student/tests/{testId}/submit-answer
```

**Description:** Submits an answer for a specific question. Can be called multiple times to change answers.

**Request Body:**
```json
{
  "sessionId": "abc-123-uuid-456",
  "questionId": 1,
  "selectedOptionId": 2
}
```

**To Skip a Question:**
```json
{
  "sessionId": "abc-123-uuid-456",
  "questionId": 5,
  "selectedOptionId": null
}
```

**Success Response (200):**
```json
{
  "message": "Answer submitted successfully"
}
```

**Error Response (400):**
```json
{
  "error": "Invalid session for this student"
}
```

---

### 6. Submit Test (Final Submission)

```
POST /api/student/tests/{testId}/submit?sessionId={sessionId}
```

**Description:** Ends the test, calculates results, and returns the final score.

**Example:**
```
POST /api/student/tests/2/submit?sessionId=abc-123-uuid-456
```

**Success Response (200):**
```json
{
  "attemptId": 1,
  "testId": 2,
  "testName": "Class 10 - Algebra Test",
  "attemptNumber": 1,
  "startedAt": "2025-10-14T10:00:00",
  "submittedAt": "2025-10-14T10:45:30",
  "timeTakenSeconds": 2730,
  "totalQuestions": 50,
  "answeredQuestions": 48,
  "correctAnswers": 42,
  "wrongAnswers": 6,
  "unansweredQuestions": 2,
  "totalMarksObtained": 165.5,
  "totalMarksAvailable": 200.0,
  "percentage": 82.75,
  "isPassed": true,
  "passingMarks": 80.0
}
```

---

### 7. Get Test Result

```
GET /api/student/tests/{testId}/result/{attemptId}
```

**Description:** Retrieves result of a submitted test attempt.

**Example:**
```
GET /api/student/tests/2/result/1
```

**Response:** Same as Submit Test response

---

### 8. Abandon Session

```
DELETE /api/student/tests/{testId}/abandon-session
```

**Description:** Cancels the active session without submitting. Use this to start fresh.

**Success Response (200):**
```json
{
  "message": "Session abandoned successfully. You can now start a new attempt."
}
```

**Error Response (400):**
```json
{
  "error": "No active session found for this test"
}
```

---

### 9. Get Test Attempt History

#### Get All Attempts for a Specific Test
```
GET /api/student/tests/{testId}/attempts
```

**Description:** Returns all submitted attempts for a specific test.

**Response:**
```json
[
  {
    "attemptId": 3,
    "testId": 2,
    "testName": "Class 10 - Algebra Test",
    "attemptNumber": 3,
    "startedAt": "2025-10-14T10:00:00",
    "submittedAt": "2025-10-14T10:45:30",
    "timeTakenSeconds": 2730,
    "totalQuestions": 50,
    "answeredQuestions": 50,
    "correctAnswers": 45,
    "wrongAnswers": 5,
    "unansweredQuestions": 0,
    "totalMarksObtained": 178.75,
    "totalMarksAvailable": 200.0,
    "percentage": 89.38,
    "isPassed": true,
    "passingMarks": 80.0
  },
  {
    "attemptId": 2,
    "testName": "Class 10 - Algebra Test",
    "attemptNumber": 2,
    "percentage": 75.5,
    "isPassed": false,
    ...
  }
]
```

---

#### Get All Attempts Across All Tests
```
GET /api/student/tests/attempts
```

**Description:** Returns all submitted attempts by the student across all tests.

**Response:** Array of test results (same structure as above)

---

## Complete Test Flow

### Flow Diagram

```
1. Student browses tests
   ↓
2. GET /api/student/dashboard/tests?classId=10
   ← Returns list of available tests
   ↓
3. Student clicks "Start Test"
   ↓
4. GET /api/student/tests/2/active-session
   ← Check if active session exists
   ↓
5a. If no active session:
    POST /api/student/tests/2/start
    ← Returns sessionId
    ↓
5b. If active session exists:
    User chooses:
    - Continue → Use existing sessionId
    - Start Fresh → DELETE /abandon-session → POST /start
    ↓
6. GET /api/student/tests/2/questions?sessionId=xxx
   ← Returns all questions
   ↓
7. Student answers questions
   For each answer:
   POST /api/student/tests/2/submit-answer
   Body: { sessionId, questionId, selectedOptionId }
   ↓
8. Student clicks "Submit Test"
   POST /api/student/tests/2/submit?sessionId=xxx
   ← Returns full test result
   ↓
9. Show results page
   (Can also use GET /api/student/tests/2/result/{attemptId})
   ↓
10. View history
    GET /api/student/tests/2/attempts
    ← Returns all previous attempts
```

---

## Error Handling

### Common HTTP Status Codes

| Status | Meaning | Action |
|--------|---------|--------|
| 200 | Success | Continue |
| 400 | Bad Request | Show error message to user |
| 401 | Unauthorized | Redirect to login |
| 403 | Forbidden | Show "No access" message |
| 500 | Server Error | Show generic error message |

### Common Errors

#### No Subscription
```json
{
  "error": "You do not have access to this test. Please purchase a subscription."
}
```
**Action:** Show subscription purchase page

#### Max Attempts Reached
```json
{
  "error": "You have reached the maximum number of attempts (3) for this test"
}
```
**Action:** Disable "Start Test" button, show message

#### Active Session Exists
```json
{
  "error": "You already have an active session for this test..."
}
```
**Action:** Show dialog with "Continue" or "Start Fresh" options

---

## Example Implementation

### React/TypeScript Example

```typescript
// types.ts
export interface TestSessionResponse {
  sessionId: string;
  attemptId: number;
  testId: number;
  testName: string;
  timeLimitMinutes: number;
  totalQuestions: number;
  attemptNumber: number;
  startedAt: string;
  expiresAt: string;
  status: string;
  negativeMarking: boolean;
  negativeMarkPercentage: number;
}

export interface TestQuestion {
  questionId: number;
  questionText: string;
  questionType: string;
  questionOrder: number;
  marks: number;
  options: QuestionOption[];
}

export interface QuestionOption {
  optionId: number;
  optionText: string;
  optionImageUrl: string | null;
}

export interface TestResult {
  attemptId: number;
  testId: number;
  testName: string;
  attemptNumber: number;
  startedAt: string;
  submittedAt: string;
  timeTakenSeconds: number;
  totalQuestions: number;
  answeredQuestions: number;
  correctAnswers: number;
  wrongAnswers: number;
  unansweredQuestions: number;
  totalMarksObtained: number;
  totalMarksAvailable: number;
  percentage: number;
  isPassed: boolean;
  passingMarks: number;
}

// api.ts
const API_BASE = 'http://localhost:8080';

const getAuthHeaders = () => ({
  'Authorization': `Bearer ${localStorage.getItem('token')}`,
  'Content-Type': 'application/json'
});

export const testAPI = {
  // Get available tests
  getTests: async (classId?: number, examId?: number) => {
    let url = `${API_BASE}/api/student/dashboard/tests`;
    const params = new URLSearchParams();
    if (classId) params.append('classId', classId.toString());
    if (examId) params.append('examId', examId.toString());
    if (params.toString()) url += `?${params.toString()}`;
    
    const response = await fetch(url, {
      headers: getAuthHeaders()
    });
    return response.json();
  },

  // Check active session
  checkActiveSession: async (testId: number) => {
    const response = await fetch(
      `${API_BASE}/api/student/tests/${testId}/active-session`,
      { headers: getAuthHeaders() }
    );
    return response.json();
  },

  // Start test
  startTest: async (testId: number) => {
    const response = await fetch(
      `${API_BASE}/api/student/tests/${testId}/start`,
      {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({
          ipAddress: '0.0.0.0', // Or get real IP
          userAgent: navigator.userAgent
        })
      }
    );
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error);
    }
    
    return response.json();
  },

  // Get questions
  getQuestions: async (testId: number, sessionId: string): Promise<TestQuestion[]> => {
    const response = await fetch(
      `${API_BASE}/api/student/tests/${testId}/questions?sessionId=${sessionId}`,
      { headers: getAuthHeaders() }
    );
    return response.json();
  },

  // Submit answer
  submitAnswer: async (testId: number, sessionId: string, questionId: number, selectedOptionId: number | null) => {
    const response = await fetch(
      `${API_BASE}/api/student/tests/${testId}/submit-answer`,
      {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({
          sessionId,
          questionId,
          selectedOptionId
        })
      }
    );
    return response.json();
  },

  // Submit test
  submitTest: async (testId: number, sessionId: string): Promise<TestResult> => {
    const response = await fetch(
      `${API_BASE}/api/student/tests/${testId}/submit?sessionId=${sessionId}`,
      {
        method: 'POST',
        headers: getAuthHeaders()
      }
    );
    return response.json();
  },

  // Abandon session
  abandonSession: async (testId: number) => {
    const response = await fetch(
      `${API_BASE}/api/student/tests/${testId}/abandon-session`,
      {
        method: 'DELETE',
        headers: getAuthHeaders()
      }
    );
    return response.json();
  },

  // Get test attempts
  getTestAttempts: async (testId: number): Promise<TestResult[]> => {
    const response = await fetch(
      `${API_BASE}/api/student/tests/${testId}/attempts`,
      { headers: getAuthHeaders() }
    );
    return response.json();
  },

  // Get all attempts
  getAllAttempts: async (): Promise<TestResult[]> => {
    const response = await fetch(
      `${API_BASE}/api/student/tests/attempts`,
      { headers: getAuthHeaders() }
    );
    return response.json();
  }
};

// TestPage.tsx - Complete Implementation
import React, { useState, useEffect } from 'react';
import { testAPI } from './api';

export const TestPage: React.FC<{ testId: number }> = ({ testId }) => {
  const [session, setSession] = useState<TestSessionResponse | null>(null);
  const [questions, setQuestions] = useState<TestQuestion[]>([]);
  const [answers, setAnswers] = useState<Map<number, number | null>>(new Map());
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [timeRemaining, setTimeRemaining] = useState(0);
  const [result, setResult] = useState<TestResult | null>(null);

  // Check for active session on mount
  useEffect(() => {
    checkSession();
  }, []);

  const checkSession = async () => {
    try {
      const activeSession = await testAPI.checkActiveSession(testId);
      
      if (activeSession.hasActiveSession === false) {
        // No active session, can start fresh
        return;
      } else {
        // Active session exists
        const shouldContinue = window.confirm(
          'You have an ongoing test. Do you want to continue?'
        );
        
        if (shouldContinue) {
          setSession(activeSession);
          loadQuestions(activeSession.sessionId);
        } else {
          await testAPI.abandonSession(testId);
          // Now can start fresh
        }
      }
    } catch (error) {
      console.error('Error checking session:', error);
    }
  };

  const startTest = async () => {
    try {
      const newSession = await testAPI.startTest(testId);
      setSession(newSession);
      setTimeRemaining(newSession.timeLimitMinutes * 60);
      loadQuestions(newSession.sessionId);
    } catch (error: any) {
      alert(error.message);
    }
  };

  const loadQuestions = async (sessionId: string) => {
    try {
      const qs = await testAPI.getQuestions(testId, sessionId);
      setQuestions(qs);
    } catch (error) {
      console.error('Error loading questions:', error);
    }
  };

  const handleAnswerSelect = async (questionId: number, optionId: number) => {
    if (!session) return;
    
    // Update local state
    setAnswers(prev => new Map(prev).set(questionId, optionId));
    
    // Submit to backend
    try {
      await testAPI.submitAnswer(testId, session.sessionId, questionId, optionId);
    } catch (error) {
      console.error('Error submitting answer:', error);
    }
  };

  const handleSubmitTest = async () => {
    if (!session) return;
    
    const confirm = window.confirm(
      'Are you sure you want to submit? You cannot change answers after submission.'
    );
    
    if (confirm) {
      try {
        const testResult = await testAPI.submitTest(testId, session.sessionId);
        setResult(testResult);
      } catch (error) {
        console.error('Error submitting test:', error);
      }
    }
  };

  // Timer effect
  useEffect(() => {
    if (session && timeRemaining > 0 && !result) {
      const timer = setInterval(() => {
        setTimeRemaining(prev => {
          if (prev <= 1) {
            handleSubmitTest(); // Auto-submit when time runs out
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
      
      return () => clearInterval(timer);
    }
  }, [session, timeRemaining, result]);

  // Render result page
  if (result) {
    return (
      <div className="test-result">
        <h1>Test Completed!</h1>
        <div className="result-card">
          <h2>{result.testName}</h2>
          <p>Attempt #{result.attemptNumber}</p>
          <div className="score">
            <h3>{result.percentage.toFixed(2)}%</h3>
            <p>{result.isPassed ? '✅ PASSED' : '❌ FAILED'}</p>
          </div>
          <div className="stats">
            <p>Correct: {result.correctAnswers}/{result.totalQuestions}</p>
            <p>Wrong: {result.wrongAnswers}</p>
            <p>Unanswered: {result.unansweredQuestions}</p>
            <p>Marks: {result.totalMarksObtained}/{result.totalMarksAvailable}</p>
            <p>Time Taken: {Math.floor(result.timeTakenSeconds / 60)} minutes</p>
          </div>
        </div>
      </div>
    );
  }

  // Render test start page
  if (!session) {
    return (
      <div className="test-start">
        <h1>Ready to start the test?</h1>
        <button onClick={startTest}>Start Test</button>
      </div>
    );
  }

  // Render test in progress
  const question = questions[currentQuestion];
  
  return (
    <div className="test-page">
      <div className="test-header">
        <h2>{session.testName}</h2>
        <div className="timer">
          Time Remaining: {Math.floor(timeRemaining / 60)}:{(timeRemaining % 60).toString().padStart(2, '0')}
        </div>
        <div className="progress">
          Question {currentQuestion + 1} of {questions.length}
        </div>
      </div>

      {question && (
        <div className="question-card">
          <h3>Question {question.questionOrder}</h3>
          <p>{question.questionText}</p>
          <p className="marks">Marks: {question.marks}</p>
          
          <div className="options">
            {question.options.map(option => (
              <label key={option.optionId} className="option">
                <input
                  type="radio"
                  name={`question-${question.questionId}`}
                  value={option.optionId}
                  checked={answers.get(question.questionId) === option.optionId}
                  onChange={() => handleAnswerSelect(question.questionId, option.optionId)}
                />
                {option.optionText}
              </label>
            ))}
          </div>
        </div>
      )}

      <div className="navigation">
        <button
          disabled={currentQuestion === 0}
          onClick={() => setCurrentQuestion(prev => prev - 1)}
        >
          Previous
        </button>
        
        <button
          onClick={() => setCurrentQuestion(prev => prev + 1)}
          disabled={currentQuestion === questions.length - 1}
        >
          Next
        </button>
        
        <button
          onClick={handleSubmitTest}
          className="submit-button"
        >
          Submit Test
        </button>
      </div>

      <div className="question-palette">
        {questions.map((q, idx) => (
          <button
            key={q.questionId}
            className={`
              palette-item
              ${idx === currentQuestion ? 'active' : ''}
              ${answers.has(q.questionId) ? 'answered' : 'unanswered'}
            `}
            onClick={() => setCurrentQuestion(idx)}
          >
            {idx + 1}
          </button>
        ))}
      </div>
    </div>
  );
};
```

---

## Best Practices

### 1. Session Management
- Always check for active sessions before starting a new test
- Store sessionId in component state or context
- Clear session data after test submission

### 2. Auto-Save Answers
- Submit answers immediately when selected
- No need for "Save" button per question
- Backend handles answer updates

### 3. Timer Management
- Implement countdown timer on frontend
- Auto-submit when time expires
- Warn user when 5 minutes remaining

### 4. Network Error Handling
- Retry failed answer submissions
- Show network status indicator
- Cache answers locally as backup

### 5. Security
- Never store JWT token in localStorage in production
- Use httpOnly cookies for authentication
- Validate all user inputs

---

## Testing Checklist

- [ ] Can view tests for subscribed class/exam
- [ ] Cannot view tests without subscription
- [ ] Can start test successfully
- [ ] Cannot start test if max attempts reached
- [ ] Can navigate between questions
- [ ] Answers are saved immediately
- [ ] Timer counts down correctly
- [ ] Auto-submits at time expiration
- [ ] Can manually submit test
- [ ] Result displays correctly
- [ ] Can view attempt history
- [ ] Can abandon and restart test
- [ ] Cannot access test without active session

---

## Support

For API issues or questions, contact the backend team.

**API Version:** 1.0
**Last Updated:** October 14, 2025

