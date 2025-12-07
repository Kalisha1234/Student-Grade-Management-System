package org.example;

public class SubjectNotFoundException extends Exception {
    private String subjectName;

    public SubjectNotFoundException(String subjectName) {
        super("Subject '" + subjectName + "' not found in the system.");
        this.subjectName = subjectName;
    }

    public String getSubjectName() {
        return subjectName;
    }
}