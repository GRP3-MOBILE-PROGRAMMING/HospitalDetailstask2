package com.example.hospitaldetails;

import java.util.Locale;

public class Patient {

    private final String id;
    private final String name;
    private final int age;
    private final String gender;
    private final String reasonForVisit;
    private final String photoName;      // name of the drawable, e.g. "sarah_namusoke"
    private final String bloodGroup;
    private final String lastVisit;
    private final String medicalHistory;
    private final String medications;
    private final String allergies;
    private final String vitals;

    public Patient(String id, String name, int age, String gender, String reasonForVisit,
                   String photoName, String bloodGroup, String lastVisit,
                   String medicalHistory, String medications, String allergies, String vitals) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.reasonForVisit = reasonForVisit;
        this.photoName = photoName;
        this.bloodGroup = bloodGroup;
        this.lastVisit = lastVisit;
        this.medicalHistory = medicalHistory;
        this.medications = medications;
        this.allergies = allergies;
        this.vitals = vitals;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getReasonForVisit() { return reasonForVisit; }
    public String getPhotoName() { return photoName; }
    public String getBloodGroup() { return bloodGroup; }
    public String getLastVisit() { return lastVisit; }
    public String getMedicalHistory() { return medicalHistory; }
    public String getMedications() { return medications; }
    public String getAllergies() { return allergies; }
    public String getVitals() { return vitals; }

    /** "F" or "M", shown on the list cards. */
    public String getGenderInitial() {
        return "Female".equalsIgnoreCase(gender) ? "F" : "M";
    }

    /** Second line of a list card, e.g. "34 years  ·  F". */
    public String getListSubtitle() {
        return age + " years  ·  " + getGenderInitial();
    }

    /** Line under the name on the detail screen. */
    public String getDetailMeta() {
        return "Patient ID: " + id + "  ·  " + gender + ", " + age + " years";
    }
    /** The complete record as plain text, shown in the full-record dialog. */
    public String getFullRecord() {
        return getDetailMeta() + "\n\n"
                + "Blood group: " + bloodGroup + "\n"
                + "Last visit: " + lastVisit + "\n"
                + "Reason for visit: " + reasonForVisit + "\n\n"
                + "MEDICAL HISTORY\n" + medicalHistory + "\n\n"
                + "MEDICATIONS\n" + medications + "\n\n"
                + "ALLERGIES\n" + allergies + "\n\n"
                + "LATEST VITAL SIGNS\n" + vitals;
    }

    /** True if the search text appears in the patient's name or ID. An empty search matches everyone. */
    public boolean matches(String query) {
        if (query == null) return true;
        String q = query.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) return true;
        return name.toLowerCase(Locale.ROOT).contains(q)
                || id.toLowerCase(Locale.ROOT).contains(q);
    }
}
