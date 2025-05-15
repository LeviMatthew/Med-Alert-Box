package com.app.medalertbox.patient

import androidx.lifecycle.LiveData

class PatientRepository(private val dao: PatientDao) {
    val allPatients: LiveData<List<Patient>> = dao.getAllPatients()

    suspend fun insertPatient(patient: Patient) {
        dao.insertPatient(patient)
    }

    suspend fun deletePatient(patient: Patient) {
        dao.deletePatient(patient)
    }
}
