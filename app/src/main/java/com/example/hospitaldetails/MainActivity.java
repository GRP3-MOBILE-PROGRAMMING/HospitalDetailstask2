package com.example.hospitaldetails;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_DETAIL = "patient_detail";
    private static final String KEY_WAS_TABLET = "was_tablet";

    private PatientViewModel viewModel;
    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // true only when the sw600dp resources are in use
        isTablet = getResources().getBoolean(R.bool.is_tablet);

        // If the screen switched between phone and tablet layouts (for example a foldable
        // unfolding), the saved fragments no longer fit the new layout. Drop them and rebuild
        // the screen from the ViewModel, which keeps the selected patient.
        boolean layoutChanged = savedInstanceState != null
                && savedInstanceState.getBoolean(KEY_WAS_TABLET, isTablet) != isTablet;
        Bundle state = layoutChanged ? null : savedInstanceState;

        super.onCreate(state);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(PatientViewModel.class);

        if (state == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(isTablet ? R.id.list_container : R.id.fragment_container,
                            new PatientListFragment())
                    .commit();
        }

        if (isTablet) {
            viewModel.ensureSelection();
        } else {
            keepSelectionInSyncWithBackStack();
        }

        viewModel.getSelectedPatientId().observe(this, this::showSelectedPatient);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_WAS_TABLET, isTablet);
    }

    /** Makes what is on screen match what the ViewModel says is selected. */
    private void showSelectedPatient(String patientId) {
        FragmentManager fm = getSupportFragmentManager();
        PatientDetailFragment shown = (PatientDetailFragment) fm.findFragmentByTag(TAG_DETAIL);

        if (patientId == null) {
            // Nothing selected. On a phone, leave the detail screen if it is showing.
            if (shown != null && !isTablet) fm.popBackStack();
            return;
        }
        if (shown != null && patientId.equals(shown.getPatientId())) {
            return; // already showing this patient (for example after a rotation)
        }

        PatientDetailFragment detail = PatientDetailFragment.newInstance(patientId);
        if (isTablet) {
            // Two panes: just swap the right pane
            fm.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.detail_container, detail, TAG_DETAIL)
                    .commit();
        } else {
            // Single pane: replace the list, and let Back return to it
            fm.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, detail, TAG_DETAIL)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /** When Back returns to the list on a phone, clear the selection too. */
    private void keepSelectionInSyncWithBackStack() {
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                viewModel.clearSelection();
            }
        });
    }
}