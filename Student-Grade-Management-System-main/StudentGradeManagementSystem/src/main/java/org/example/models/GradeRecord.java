package org.example.models;

public class GradeRecord {
    private String studentId;
    private String subjectName;
    private String subjectType;
    private double grade;
    private int rowNumber;

    public GradeRecord(String studentId, String subjectName, String subjectType, double grade, int rowNumber) {
        this.studentId = studentId;
        this.subjectName = subjectName;
        this.subjectType = subjectType;
        this.grade = grade;
        this.rowNumber = rowNumber;
    }

    // Getters
    public String getStudentId() { return studentId; }
    public String getSubjectName() { return subjectName; }
    public String getSubjectType() { return subjectType; }
    public double getGrade() { return grade; }
    public int getRowNumber() { return rowNumber; }
}