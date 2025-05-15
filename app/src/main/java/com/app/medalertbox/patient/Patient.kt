package com.app.medalertbox.patient

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "patients")
data class Patient(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,  // This is local DB ID; Firebase will have its own UID
    
    var firstName: String = "",
    var middleName: String = "",
    var lastName: String = "",
    var patientNumber: String = "",
    var age: Int,
    var healthCondition: String = "",
    var address: String = "",
    var relativeName: String = "",
    var relativeContact: String = "",
    var profileImageUri: String? = ""

) {
    // Firebase requires a no-argument constructor
    constructor() : this(
        0, "", "", "", "", 0, "", "", "", "", ""
    )
}
