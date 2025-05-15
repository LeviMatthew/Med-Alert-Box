package com.app.medalertbox

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.medalertbox.databinding.ActivityPatientProfileBinding

class PatientProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientProfileBinding

    private var firstName: String? = null
    private var middleName: String? = null
    private var lastName: String? = null
    private var patientNumber: String? = null
    private var profileImageUri: String? = null
    private var healthCondition: String? = null
    private var age: String? = null
    private var address: String? = null
    private var relativeName: String? = null
    private var relativeContact: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Extract data from intent
        extractIntentData()

        // Show patient data in views
        displayPatientInfo()

        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Delete confirmation dialog
        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Go to PersonalInformationActivity with all patient details
        binding.btnPatientInfo.setOnClickListener {
            val fullName = listOfNotNull(
                firstName?.takeIf { it.isNotBlank() },
                middleName?.takeIf { it.isNotBlank() },
                lastName?.takeIf { it.isNotBlank() }
            ).joinToString(" ").ifEmpty { "Name not available" }

            val intent = Intent(this, PersonalInformationActivity::class.java).apply {
                putExtra("profileImageUri", profileImageUri)
                putExtra("fullName", fullName)
                putExtra("patientNumber", patientNumber)
                putExtra("healthCondition", healthCondition ?: "Unknown")
                putExtra("age", age ?: "Unknown")
                putExtra("address", address ?: "Unknown")
                putExtra("relativeName", relativeName ?: "Unknown")
                putExtra("relativeContact", relativeContact ?: "Unknown")
            }
            startActivity(intent)
        }

        // NEW: Go to ListOfMedicationsActivity
        binding.btnListMedications.setOnClickListener {
            val intent = Intent(this, ListOfMedicationsActivity::class.java).apply {
                putExtra("patientNumber", patientNumber) // Optional: for filtering if implemented
            }
            startActivity(intent)
        }
    }

    private fun extractIntentData() {
        intent?.let {
            firstName = it.getStringExtra("firstName")
            middleName = it.getStringExtra("middleName")
            lastName = it.getStringExtra("lastName")
            patientNumber = it.getStringExtra("patientNumber")
            profileImageUri = it.getStringExtra("profileImageUri")
            healthCondition = it.getStringExtra("healthCondition")
            age = it.getStringExtra("age")
            address = it.getStringExtra("address")
            relativeName = it.getStringExtra("relativeName")
            relativeContact = it.getStringExtra("relativeContact")
        }
    }

    private fun displayPatientInfo() {
        val fullName = listOfNotNull(
            firstName?.takeIf { it.isNotBlank() },
            middleName?.takeIf { it.isNotBlank() },
            lastName?.takeIf { it.isNotBlank() }
        ).joinToString(" ").ifEmpty { "Name not available" }

        binding.tvPatientName.text = fullName
        binding.patientNumber.text = "Patient No: ${patientNumber ?: "N/A"}"

        if (!profileImageUri.isNullOrBlank()) {
            try {
                binding.imgProfile.setImageURI(Uri.parse(profileImageUri))
            } catch (e: Exception) {
                binding.imgProfile.setImageResource(R.drawable.icusers)
            }
        } else {
            binding.imgProfile.setImageResource(R.drawable.icusers)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Confirm Deletion")
            setMessage("Are you sure you want to delete this patient?")
            setPositiveButton("Delete") { _, _ ->
                // TODO: Add logic to delete the patient record from DB if needed
                finish()
            }
            setNegativeButton("Cancel", null)
            show()
        }
    }
}
