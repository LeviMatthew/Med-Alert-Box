package com.app.medalertbox.patient

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PatientDao {

    // Inserts a patient record
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient)

    // Deletes a specific patient
    @Delete
    suspend fun deletePatient(patient: Patient)

    // Retrieves all patients sorted by name
    @Query("SELECT * FROM patients ORDER BY firstName ASC, middleName ASC, lastName ASC")
    fun getAllPatients(): LiveData<List<Patient>>

    // Retrieves the highest patient number from the database
    @Query("SELECT patientNumber FROM patients ORDER BY patientNumber DESC LIMIT 1")
    suspend fun getLatestPatientNumber(): String?
}
