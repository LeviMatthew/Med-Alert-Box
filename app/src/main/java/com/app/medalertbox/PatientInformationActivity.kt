package com.app.medalertbox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var btnViewDetails: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_information)

        // Initialize views
        imgProfile = findViewById(R.id.imgProfile)
        txtFullName = findViewById(R.id.txtFullName)
        txtPatientNumber = findViewById(R.id.txtPatientNumber)
        txtAge = findViewById(R.id.txtAge)
        txtHealthCondition = findViewById(R.id.txtHealthCondition)
        txtAddress = findViewById(R.id.txtAddress)
        txtRelativeName = findViewById(R.id.txtRelativeName)
        txtRelativeContact = findViewById(R.id.txtRelativeContact)

        // Get data from intent
        val patientId = intent.getStringExtra("patientId")  // 🔑 Add this line
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

        // Full name construction
        val fullName = listOf(firstName, middleName, lastName).filter { it.isNotBlank() }.joinToString(" ")

        // Set data to views
        txtFullName.text = fullName.ifBlank { "Name not available" }
        txtPatientNumber.text = "Patient #: $patientNumber"
        txtAge.text = "Age: $age"
        txtHealthCondition.text = "Health Condition: $healthCondition"
        txtAddress.text = "Address: $address"
        txtRelativeName.text = "Relative: $relativeName"
        txtRelativeContact.text = "Relative Contact: $relativeContact"

        // Set profile image
        profileImageUri?.let {
            try {
                imgProfile.setImageURI(Uri.parse(it))
            } catch (e: Exception) {
                imgProfile.setImageResource(R.drawable.icusers)
            }
        } ?: imgProfile.setImageResource(R.drawable.icusers)

        // View more details
        btnViewDetails.setOnClickListener {
            if (patientId.isNullOrBlank()) {
                Toast.makeText(this, "Patient ID is missing!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, PersonalInformationActivity::class.java).apply {
                putExtra("profileImageUri", profileImageUri)
                putExtra("fullName", fullName)
                putExtra("patientNumber", patientNumber)
                putExtra("healthCondition", healthCondition)
                putExtra("age", age)
                putExtra("patientId", patientId) // 🔑 Pass patientId here
            }
            startActivity(intent)
        }
    }
}
