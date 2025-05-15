package com.app.medalertbox.patient

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PatientDao {

    // Inserts or updates a patient record
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient)

    // Deletes a specific patient
    @Delete
    suspend fun deletePatient(patient: Patient)

    // Retrieves all patients sorted by full name (first + middle + last)
    @Query("SELECT * FROM patients ORDER BY firstName ASC, middleName ASC, lastName ASC")
    fun getAllPatients(): LiveData<List<Patient>>
}
