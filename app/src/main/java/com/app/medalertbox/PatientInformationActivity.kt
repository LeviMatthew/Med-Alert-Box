package com.app.medalertbox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PatientInformationActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var txtFullName: TextView
    private lateinit var txtPatientNumber: TextView
    private lateinit var txtAge: TextView
    private lateinit var txtHealthCondition: TextView
    private lateinit var txtAddress: TextView
    private lateinit var txtRelativeName: TextView
    private lateinit var txtRelativeContact: TextView
    private lateinit var btnViewDetails: Button // Button to trigger PersonalInformationActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_information)

        imgProfile = findViewById(R.id.imgProfile)
        txtFullName = findViewById(R.id.txtFullName)
        txtPatientNumber = findViewById(R.id.txtPatientNumber)
        txtAge = findViewById(R.id.txtAge)
        txtHealthCondition = findViewById(R.id.txtHealthCondition)
        txtAddress = findViewById(R.id.txtAddress)
        txtRelativeName = findViewById(R.id.txtRelativeName)
        txtRelativeContact = findViewById(R.id.txtRelativeContact)

        // Get data from intent
        val firstName = intent.getStringExtra("firstName") ?: ""
        val middleName = intent.getStringExtra("middleName") ?: ""
        val lastName = intent.getStringExtra("lastName") ?: ""
        val patientNumber = intent.getStringExtra("patientNumber") ?: "N/A"
        val age = intent.getStringExtra("age") ?: "N/A"
        val healthCondition = intent.getStringExtra("healthCondition") ?: "N/A"
        val address = intent.getStringExtra("address") ?: "N/A"
        val relativeName = intent.getStringExtra("relativeName") ?: "N/A"
        val relativeContact = intent.getStringExtra("relativeContact") ?: "N/A"
        val profileImageUri = intent.getStringExtra("profileImageUri")

        // Build full name
        val fullName = listOf(firstName, middleName, lastName).filter { it.isNotBlank() }.joinToString(" ")

        // Set text views
        txtFullName.text = fullName.ifBlank { "Name not available" }
        txtPatientNumber.text = "Patient #: $patientNumber"
        txtAge.text = "Age: $age"
        txtHealthCondition.text = "Health Condition: $healthCondition"
        txtAddress.text = "Address: $address"
        txtRelativeName.text = "Relative: $relativeName"
        txtRelativeContact.text = "Relative Contact: $relativeContact"

        // Set image
        profileImageUri?.let {
            try {
                imgProfile.setImageURI(Uri.parse(it))
            } catch (e: Exception) {
                imgProfile.setImageResource(R.drawable.icusers)
            }
        } ?: imgProfile.setImageResource(R.drawable.icusers)

        // 🔁 Launch PersonalInformationActivity
        btnViewDetails.setOnClickListener {
            val intent = Intent(this, PersonalInformationActivity::class.java).apply {
                putExtra("profileImageUri", profileImageUri)
                putExtra("fullName", fullName)
                putExtra("patientNumber", patientNumber)
                putExtra("healthCondition", healthCondition)
                putExtra("age", age)
            }
            startActivity(intent)
        }
    }
}
