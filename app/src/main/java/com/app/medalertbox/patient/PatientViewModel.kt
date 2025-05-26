package com.app.medalertbox.patient

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PatientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PatientRepository
    val allPatients: LiveData<List<Patient>>
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        val dao = PatientDatabase.getDatabase(application).patientDao()
        repository = PatientRepository(dao)
        allPatients = repository.allPatients
    }

    fun insert(patient: Patient) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val patientNumber = if (patient.patientNumber.isBlank()) {
                    repository.getNextPatientNumber()
                } else {
                    patient.patientNumber
                }

                val updatedPatient = patient.copy(patientNumber = patientNumber)
                repository.insertPatient(updatedPatient) // Save to Room
                uploadPatientToFirestore(updatedPatient) // Upload to Firestore
            } catch (e: Exception) {
                Log.e("PatientViewModel", "Failed to insert patient", e)
            }
        }
    }

    fun delete(patient: Patient) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deletePatient(patient) // Delete from Room
                deletePatientFromFirestore(patient) // Delete from Firestore
            } catch (e: Exception) {
                Log.e("PatientViewModel", "Failed to delete patient", e)
            }
        }
    }

    private fun uploadPatientToFirestore(patient: Patient) {
        val docId = patient.patientNumber.ifBlank { System.currentTimeMillis().toString() }

        firestore.collection("patients")
            .document(docId)
            .set(patient)
            .addOnSuccessListener {
                Log.d("PatientViewModel", "Patient uploaded to Firestore: $docId")
            }
            .addOnFailureListener { e ->
                Log.e("PatientViewModel", "Failed to upload patient to Firestore", e)
            }
    }

    private fun deletePatientFromFirestore(patient: Patient) {
        val docId = patient.patientNumber.ifBlank { return }

        firestore.collection("patients")
            .document(docId)
            .delete()
            .addOnSuccessListener {
                Log.d("PatientViewModel", "Patient deleted from Firestore: $docId")
            }
            .addOnFailureListener { e ->
                Log.e("PatientViewModel", "Failed to delete patient from Firestore", e)
            }
    }
}
