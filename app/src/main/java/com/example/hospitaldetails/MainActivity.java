package com.example.hospitaldetails;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements PatientListFragment.OnPatientSelectedListener {
    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isTablet = findViewById(R.id.detail_container) != null;
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(isTablet ? R.id.list_container : R.id.fragment_container, new PatientListFragment())
                    .commit();
            if (isTablet) {
                String sarahDetails = "sarah_namusoke|O+|12 Oct 2026|Hypertension — diagnosed 2023\nFollow-up scheduled with clinician|Amlodipine — dosage per prescription\nMedication review required|No known allergies recorded|Blood pressure: 138/88 mmHg\nPulse: 78 bpm  ·  Temperature: 36.8 °C";
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, PatientDetailFragment.newInstance(
                                "Sarah Namusoke", "34", "Female", "UHC-001", "Hypertension follow-up", sarahDetails))
                        .commit();
            }
        }
    }

    @Override
    public void onPatientSelected(String name, String age, String gender, String id, String condition, String phone) {
        PatientDetailFragment details = PatientDetailFragment.newInstance(name, age, gender, id, condition, phone);
        if (isTablet) {
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, details).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, details).addToBackStack(null).commit();
        }
    }
}
