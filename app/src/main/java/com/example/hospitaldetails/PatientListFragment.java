package com.example.hospitaldetails;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PatientListFragment extends Fragment {
    public interface OnPatientSelectedListener {
        void onPatientSelected(String name, String age, String gender, String id, String condition, String phone);
    }

    private OnPatientSelectedListener listener;
    private LinearLayout patientList;
    private EditText searchField;

    // Demonstration data only. The medical records and patient portraits are fictional.
    private final String[][] patients = {
        {"Sarah Namusoke", "34", "Female", "UHC-001", "Hypertension follow-up", "sarah_namusoke", "O+", "12 Oct 2026", "Hypertension — diagnosed 2023\nFollow-up scheduled with clinician", "Amlodipine — dosage per prescription\nMedication review required", "No known allergies recorded", "Blood pressure: 138/88 mmHg\nPulse: 78 bpm  ·  Temperature: 36.8 °C"},
        {"John Okello", "52", "Male", "UHC-002", "Diabetes review", "john_okello", "A+", "08 Oct 2026", "Type 2 diabetes — routine review", "Medication review at next visit", "No known allergies recorded", "Blood pressure: 126/82 mmHg\nPulse: 76 bpm  ·  Temperature: 36.7 °C"},
        {"Amina Nakato", "27", "Female", "UHC-003", "Antenatal visit", "amina_nakato", "B+", "15 Oct 2026", "Antenatal care — routine follow-up", "Prenatal supplements as prescribed", "No known allergies recorded", "Blood pressure: 112/72 mmHg\nPulse: 80 bpm  ·  Temperature: 36.6 °C"}
    };

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        if (context instanceof OnPatientSelectedListener) listener = (OnPatientSelectedListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        patientList = view.findViewById(R.id.patient_list);
        searchField = view.findViewById(R.id.search_field);
        showPatients("");
        searchField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { showPatients(s.toString()); }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void showPatients(String query) {
        patientList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        String q = query.toLowerCase().trim();
        for (String[] patient : patients) {
            if (patient[0].toLowerCase().contains(q) || patient[3].toLowerCase().contains(q)) {
                View card = inflater.inflate(R.layout.item_patient, patientList, false);
                ((TextView) card.findViewById(R.id.patient_name)).setText(patient[0]);
                ((TextView) card.findViewById(R.id.patient_demographics)).setText(patient[1] + " years  ·  " + (patient[2].equals("Female") ? "F" : "M"));
                ((TextView) card.findViewById(R.id.patient_id)).setText(patient[3]);
                ((TextView) card.findViewById(R.id.patient_reason)).setText(patient[4]);
                ImageView photo = card.findViewById(R.id.patient_photo);
                int imageRes = getResources().getIdentifier(patient[5], "drawable", requireContext().getPackageName());
                if (imageRes != 0) photo.setImageResource(imageRes);
                card.setOnClickListener(v -> {
                    if (listener != null) {
                        String details = patient[5] + "|" + patient[6] + "|" + patient[7] + "|" + patient[8] + "|" + patient[9] + "|" + patient[10] + "|" + patient[11];
                        listener.onPatientSelected(patient[0], patient[1], patient[2], patient[3], patient[4], details);
                    }
                });
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = dp(14);
                card.setLayoutParams(params);
                patientList.addView(card);
            }
        }
    }

    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density + 0.5f); }
}
