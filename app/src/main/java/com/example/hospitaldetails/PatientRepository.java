package com.example.hospitaldetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatientRepository {

    private static PatientRepository instance;

    /** One shared repository for the whole app. */
    public static synchronized PatientRepository getInstance() {
        if (instance == null) {
            instance = new PatientRepository(createSamplePatients());
        }
        return instance;
    }

    private final List<Patient> patients;

    public PatientRepository(List<Patient> patients) {
        this.patients = Collections.unmodifiableList(new ArrayList<>(patients));
    }

    /** All patients, in display order. */
    public List<Patient> getAll() {
        return patients;
    }

    /** Returns the patient with this id, or null if there is none. */
    public Patient findById(String id) {
        if (id == null) return null;
        for (Patient p : patients) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    /** Patients whose name or ID contains the search text. Empty text returns everyone. */
    public List<Patient> search(String query) {
        List<Patient> result = new ArrayList<>();
        for (Patient p : patients) {
            if (p.matches(query)) result.add(p);
        }
        return result;
    }

    // Demonstration data only. The medical records and patient portraits are fictional.
    private static List<Patient> createSamplePatients() {
        List<Patient> list = new ArrayList<>();

        list.add(new Patient(
                "UHC-001", "Sarah Namusoke", 34, "Female", "Hypertension follow-up",
                "sarah_namusoke", "O+", "12 Oct 2026",
                "Hypertension — diagnosed 2023\nFollow-up scheduled with clinician",
                "Amlodipine — dosage per prescription\nMedication review required",
                "No known allergies recorded",
                "Blood pressure: 138/88 mmHg\nPulse: 78 bpm  ·  Temperature: 36.8 °C"));

        list.add(new Patient(
                "UHC-002", "John Okello", 52, "Male", "Diabetes review",
                "john_okello", "A+", "08 Oct 2026",
                "Type 2 diabetes — routine review",
                "Medication review at next visit",
                "No known allergies recorded",
                "Blood pressure: 126/82 mmHg\nPulse: 76 bpm  ·  Temperature: 36.7 °C"));

        list.add(new Patient(
                "UHC-003", "Amina Nakato", 27, "Female", "Antenatal visit",
                "amina_nakato", "B+", "15 Oct 2026",
                "Antenatal care — routine follow-up",
                "Prenatal supplements as prescribed",
                "No known allergies recorded",
                "Blood pressure: 112/72 mmHg\nPulse: 80 bpm  ·  Temperature: 36.6 °C"));

        return list;
    }
}