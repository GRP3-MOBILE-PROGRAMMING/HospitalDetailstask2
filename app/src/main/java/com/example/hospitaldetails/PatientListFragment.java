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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class PatientListFragment extends Fragment {

    private PatientViewModel viewModel;
    private LinearLayout patientList;
    private TextView countLabel;
    private String selectedId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(PatientViewModel.class);

        patientList = view.findViewById(R.id.patient_list);
        countLabel = view.findViewById(R.id.patient_count);
        EditText searchField = view.findViewById(R.id.search_field);

        searchField.setText(viewModel.getCurrentQuery());
        searchField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        viewModel.getSelectedPatientId().observe(getViewLifecycleOwner(), id -> {
            selectedId = id;
            refreshHighlight();
        });
        viewModel.getVisiblePatients().observe(getViewLifecycleOwner(), this::showPatients);
    }

    private void showPatients(List<Patient> patients) {
        patientList.removeAllViews();
        countLabel.setText(getResources().getQuantityString(
                R.plurals.patient_count, patients.size(), patients.size()));

        if (patients.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText(R.string.no_patients_found);
            empty.setTextColor(ContextCompat.getColor(requireContext(), R.color.uhc_muted));
            empty.setTextSize(14);
            patientList.addView(empty);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (Patient patient : patients) {
            View card = inflater.inflate(R.layout.item_patient, patientList, false);
            card.setTag(patient.getId());

            ((TextView) card.findViewById(R.id.patient_name)).setText(patient.getName());
            ((TextView) card.findViewById(R.id.patient_demographics)).setText(patient.getListSubtitle());
            ((TextView) card.findViewById(R.id.patient_id)).setText(patient.getId());
            ((TextView) card.findViewById(R.id.patient_reason)).setText(patient.getReasonForVisit());

            ImageView photo = card.findViewById(R.id.patient_photo);
            int imageRes = getResources().getIdentifier(
                    patient.getPhotoName(), "drawable", requireContext().getPackageName());
            if (imageRes != 0) photo.setImageResource(imageRes);

            card.setOnClickListener(v -> viewModel.selectPatient(patient.getId()));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = dp(14);
            card.setLayoutParams(params);
            patientList.addView(card);
        }
        refreshHighlight();
    }

    /** Gives the selected patient's card the highlighted background. */
    private void refreshHighlight() {
        if (patientList == null) return;
        for (int i = 0; i < patientList.getChildCount(); i++) {
            View card = patientList.getChildAt(i);
            boolean selected = card.getTag() != null && card.getTag().equals(selectedId);
            card.setBackgroundResource(selected ? R.drawable.uhc_card_selected : R.drawable.uhc_card);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}