package org.example.models;

import java.io.Serializable;

public abstract class Subject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String subjectName;
    private String subjectCode;

    public Subject(String subjectName, String subjectCode) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }

    // Getters
    public String getSubjectName() { return subjectName; }
    public String getSubjectCode() { return subjectCode; }

    // Abstract methods
    public abstract void displaySubjectDetails();
    public abstract String getSubjectType();
}