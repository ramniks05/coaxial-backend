package com.coaxial.dto;

public class UserCountResponse {
    
    private long totalUsers;
    private long adminCount;
    private long instructorCount;
    private long studentCount;
    private long activeUsers;
    private long inactiveUsers;
    
    // Constructors
    public UserCountResponse() {}
    
    public UserCountResponse(long totalUsers, long adminCount, long instructorCount, 
                           long studentCount, long activeUsers, long inactiveUsers) {
        this.totalUsers = totalUsers;
        this.adminCount = adminCount;
        this.instructorCount = instructorCount;
        this.studentCount = studentCount;
        this.activeUsers = activeUsers;
        this.inactiveUsers = inactiveUsers;
    }
    
    // Getters and Setters
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    
    public long getAdminCount() { return adminCount; }
    public void setAdminCount(long adminCount) { this.adminCount = adminCount; }
    
    public long getInstructorCount() { return instructorCount; }
    public void setInstructorCount(long instructorCount) { this.instructorCount = instructorCount; }
    
    public long getStudentCount() { return studentCount; }
    public void setStudentCount(long studentCount) { this.studentCount = studentCount; }
    
    public long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
    
    public long getInactiveUsers() { return inactiveUsers; }
    public void setInactiveUsers(long inactiveUsers) { this.inactiveUsers = inactiveUsers; }
}
