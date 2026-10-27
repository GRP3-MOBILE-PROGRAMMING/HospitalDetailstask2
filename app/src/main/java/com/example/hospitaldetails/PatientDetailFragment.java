package com.example.hospitaldetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PatientDetailFragment extends Fragment {
    public static PatientDetailFragment newInstance(String name, String age, String gender, String id, String condition, String phone) {
        PatientDetailFragment fragment = new PatientDetailFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("age", age);
        args.putString("gender", gender);
        args.putString("id", id);
        args.putString("condition", condition);
        args.putString("phone", phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null) return;
        String name = args.getString("name", "Sarah Namusoke");
        String age = args.getString("age", "34");
        String gender = args.getString("gender", "Female");
        String id = args.getString("id", "UHC-001");
        String condition = args.getString("condition", "Hypertension follow-up");
        String packed = args.getString("phone", "sarah_namusoke|O+|12 Oct 2026|Hypertension — diagnosed 2023\nFollow-up scheduled with clinician|Amlodipine — dosage per prescription\nMedication review required|No known allergies recorded|Blood pressure: 138/88 mmHg\nPulse: 78 bpm  ·  Temperature: 36.8 °C");
        String[] data = packed.split("\\|", -1);
        String image = data.length > 0 ? data[0] : "sarah_namusoke";

        ((TextView) view.findViewById(R.id.detail_name)).setText(name);
        ((TextView) view.findViewById(R.id.detail_meta)).setText("Patient ID: " + id + "  ·  " + gender + ", " + age + " years");
        int imageRes = getResources().getIdentifier(image, "drawable", requireContext().getPackageName());
        if (imageRes != 0) ((ImageView) view.findViewById(R.id.detail_photo)).setImageResource(imageRes);
        ((TextView) view.findViewById(R.id.detail_blood_group)).setText(data.length > 1 ? data[1] : "O+");
        ((TextView) view.findViewById(R.id.detail_last_visit)).setText(data.length > 2 ? data[2] : "12 Oct 2026");
        ((TextView) view.findViewById(R.id.detail_condition)).setText(data.length > 3 ? data[3] : condition);
        ((TextView) view.findViewById(R.id.detail_phone)).setText(data.length > 4 ? data[4] : "Medication review required");
        ((TextView) view.findViewById(R.id.detail_allergies)).setText(data.length > 5 ? data[5] : "No known allergies recorded");
        ((TextView) view.findViewById(R.id.detail_vitals)).setText(data.length > 6 ? data[6] : "Vitals not recorded");
    }
}
