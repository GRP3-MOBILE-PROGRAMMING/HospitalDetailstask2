package com.example.hospitaldetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PatientViewModel extends ViewModel {

    private final PatientRepository repository;

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<List<Patient>> visiblePatients = new MutableLiveData<>();
    private final MutableLiveData<String> selectedPatientId = new MutableLiveData<>();

    /** Used by Android. */
    public PatientViewModel() {
        this(PatientRepository.getInstance());
    }

    /** Lets unit tests supply their own repository. */
    public PatientViewModel(PatientRepository repository) {
        this.repository = repository;
        visiblePatients.setValue(repository.getAll());
    }

    // ----- Observed by the UI -----

    public LiveData<List<Patient>> getVisiblePatients() { return visiblePatients; }

    /** The id of the selected patient, or null if none is selected. */
    public LiveData<String> getSelectedPatientId() { return selectedPatientId; }

    // ----- Current values (null-safe helpers) -----

    public String getCurrentQuery() {
        String q = searchQuery.getValue();
        return q == null ? "" : q;
    }

    /** The selected patient, or null if none is selected. */
    public Patient getSelectedPatient() {
        return repository.findById(selectedPatientId.getValue());
    }

    /** Total number of patients, ignoring the search filter. */
    public int getTotalPatientCount() {
        return repository.getAll().size();
    }

    // ----- Actions called by the UI -----

    public void setSearchQuery(String query) {
        String q = query == null ? "" : query;
        if (q.equals(getCurrentQuery()) && visiblePatients.getValue() != null) {
            return; // nothing changed (for example when the screen is restored after rotation)
        }
        searchQuery.setValue(q);
        visiblePatients.setValue(repository.search(q));
    }

    public void selectPatient(String patientId) {
        selectedPatientId.setValue(patientId);
    }

    public void clearSelection() {
        selectedPatientId.setValue(null);
    }
    /** On tablets a patient is always on screen: select the first one if nothing is selected yet. */
    public void ensureSelection() {
        if (selectedPatientId.getValue() != null) return;
        List<Patient> all = repository.getAll();
        if (!all.isEmpty()) {
            selectedPatientId.setValue(all.get(0).getId());
        }
    }
}