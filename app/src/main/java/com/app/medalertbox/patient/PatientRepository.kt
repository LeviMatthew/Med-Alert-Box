package com.app.medalertbox.patient

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PatientRepository(private val dao: PatientDao) {

    val allPatients: LiveData<List<Patient>> = dao.getAllPatients()

    suspend fun insertPatient(patient: Patient) {
        // Save locally to Room
        dao.insertPatient(patient)

        // Save remotely to Firebase
        savePatientToFirebase(patient)
    }

    suspend fun deletePatient(patient: Patient) {
        dao.deletePatient(patient)
        // Optional: Delete from Firebase (if needed)
        // deletePatientFromFirebase(patient)
    }

    suspend fun getNextPatientNumber(): String {
        val latestNumber = dao.getLatestPatientNumber()
        val nextNumber = if (latestNumber != null) {
            try {
                val numeric = latestNumber.toInt()
                String.format("%03d", numeric + 1)
            } catch (e: NumberFormatException) {
                "001"
            }
        } else {
            "001"
        }
        return nextNumber
    }

    private suspend fun savePatientToFirebase(patient: Patient) {
        withContext(Dispatchers.IO) {
            try {
                val databaseRef = FirebaseDatabase.getInstance().getReference("patients")
                val key = databaseRef.push().key ?: return@withContext
                databaseRef.child(key).setValue(patient)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Patient saved to Firebase successfully.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firebase", "Error saving patient to Firebase", e)
                    }
            } catch (e: Exception) {
                Log.e("Firebase", "Firebase write failed", e)
            }
        }
    }
}
