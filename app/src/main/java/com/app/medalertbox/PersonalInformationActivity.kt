package com.app.medalertbox

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.medalertbox.databinding.ActivityPersonalInformationBinding

class PersonalInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        extractAndDisplayIntentData()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun extractAndDisplayIntentData() {
        try {
            val profileImageUri = intent.getStringExtra("profileImageUri")
            val fullName = intent.getStringExtra("fullName")
            val patientNumber = intent.getStringExtra("patientNumber")
            val healthCondition = intent.getStringExtra("healthCondition")
            val ageStr = intent.getStringExtra("age")?.trim()

            Log.d("PersonalInfoActivity", "Intent Data -> Name: $fullName, Age: $ageStr, Number: $patientNumber")

            // Profile image
            if (!profileImageUri.isNullOrEmpty()) {
                try {
                    binding.imgProfile.setImageURI(Uri.parse(profileImageUri))
                } catch (e: Exception) {
                    Log.e("PersonalInfoActivity", "Failed to load image URI", e)
                    binding.imgProfile.setImageResource(R.drawable.icusers)
                }
            } else {
                binding.imgProfile.setImageResource(R.drawable.icusers)
            }

            // Text fields
            binding.tvFullName.text = fullName ?: "Name not available"
            binding.tvPatientNumber.text = "Patient No: ${patientNumber ?: "N/A"}"
            binding.tvHealthCondition.text = "Condition: ${healthCondition ?: "N/A"}"

            if (!ageStr.isNullOrEmpty()) {
                val age = ageStr.toIntOrNull()
                binding.tvAge.text = if (age != null && age >= 0) "Age: $age" else "Age: Invalid"
            } else {
                binding.tvAge.text = "Age: Unknown"
            }

        } catch (e: Exception) {
            Log.e("PersonalInfoActivity", "Error extracting intent data", e)
            Toast.makeText(this, "Failed to load patient data.", Toast.LENGTH_LONG).show()
        }
    }
}
