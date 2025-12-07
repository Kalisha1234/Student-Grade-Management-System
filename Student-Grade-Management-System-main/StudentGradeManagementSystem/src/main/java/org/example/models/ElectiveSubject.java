package org.example.models;

public class ElectiveSubject extends Subject {
    private boolean mandatory = false;

    public ElectiveSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }

    @Override
    public void displaySubjectDetails() {
        System.out.println("Subject: " + getSubjectName());
        System.out.println("Code: " + getSubjectCode());
        System.out.println("Type: " + getSubjectType());
        System.out.println("Mandatory: " + (isMandatory() ? "Yes" : "No"));
    }

    @Override
    public String getSubjectType() {
        return "Elective";
    }

    public boolean isMandatory() {
        return mandatory;
    }
}