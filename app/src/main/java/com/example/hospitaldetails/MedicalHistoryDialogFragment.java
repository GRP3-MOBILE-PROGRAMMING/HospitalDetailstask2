package com.example.hospitaldetails;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MedicalHistoryDialogFragment extends DialogFragment {

    private static final String ARG_PATIENT_ID = "patient_id";

    public static MedicalHistoryDialogFragment newInstance(String patientId) {
        MedicalHistoryDialogFragment fragment = new MedicalHistoryDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        String id = args == null ? null : args.getString(ARG_PATIENT_ID);
        Patient patient = PatientRepository.getInstance().findById(id);

        String title = patient == null ? "Patient not found" : patient.getName() + " — full record";
        String message = patient == null ? "" : patient.getFullRecord();

        return new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.full_record_close, null)
                .create();
    }
}