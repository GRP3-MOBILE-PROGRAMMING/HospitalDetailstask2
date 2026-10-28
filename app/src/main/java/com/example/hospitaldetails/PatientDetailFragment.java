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

    private static final String ARG_PATIENT_ID = "patient_id";

    public static PatientDetailFragment newInstance(String patientId) {
        PatientDetailFragment fragment = new PatientDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    /** The id of the patient this fragment shows. */
    public String getPatientId() {
        Bundle args = getArguments();
        return args == null ? null : args.getString(ARG_PATIENT_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Patient patient = PatientRepository.getInstance().findById(getPatientId());

        if (patient == null) {
            ((TextView) view.findViewById(R.id.detail_name)).setText("Patient not found");
            ((TextView) view.findViewById(R.id.detail_meta)).setText("");
            return;
        }

        ((TextView) view.findViewById(R.id.detail_name)).setText(patient.getName());
        ((TextView) view.findViewById(R.id.detail_meta)).setText(patient.getDetailMeta());

        int imageRes = getResources().getIdentifier(
                patient.getPhotoName(), "drawable", requireContext().getPackageName());
        if (imageRes != 0) {
            ((ImageView) view.findViewById(R.id.detail_photo)).setImageResource(imageRes);
        }

        ((TextView) view.findViewById(R.id.detail_blood_group)).setText(patient.getBloodGroup());
        ((TextView) view.findViewById(R.id.detail_last_visit)).setText(patient.getLastVisit());
        ((TextView) view.findViewById(R.id.detail_condition)).setText(patient.getMedicalHistory());
        // Despite its name, detail_phone is the medications text view in the layout
        ((TextView) view.findViewById(R.id.detail_phone)).setText(patient.getMedications());
        ((TextView) view.findViewById(R.id.detail_allergies)).setText(patient.getAllergies());
        ((TextView) view.findViewById(R.id.detail_vitals)).setText(patient.getVitals());

        view.findViewById(R.id.detail_full_history).setOnClickListener(v ->
                MedicalHistoryDialogFragment.newInstance(patient.getId())
                        .show(getParentFragmentManager(), "full_history"));
    }
}