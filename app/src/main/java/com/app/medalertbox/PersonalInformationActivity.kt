package com.app.medalertbox

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class PersonalInformationActivity : AppCompatActivity() {

    private lateinit var imageProfile: ImageView
    private lateinit var textFullName: TextView
    private lateinit var textPatientNumber: TextView
    private lateinit var textAge: TextView
    private lateinit var textHealthCondition: TextView
    private lateinit var textAddress: TextView
    private lateinit var textRelativeName: TextView
    private lateinit var textRelativeContact: TextView

    private var patientNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_information)

        // Initialize views
        imageProfile = findViewById(R.id.imgProfile)
        textFullName = findViewById(R.id.tvFullName)
        textPatientNumber = findViewById(R.id.tvPatientNumber)
        textAge = findViewById(R.id.tvAge)
        textHealthCondition = findViewById(R.id.tvHealthCondition)

        try {
            // Get all patient data from the intent
            val profileImageUri = intent.getStringExtra("profileImageUri")
            val fullName = intent.getStringExtra("fullName") ?: "N/A"
            patientNumber = intent.getStringExtra("patientNumber") ?: "N/A"
            val healthCondition = intent.getStringExtra("healthCondition") ?: "N/A"
            val age = intent.getStringExtra("age") ?: "N/A"
            val address = intent.getStringExtra("address") ?: "N/A"
            val relativeName = intent.getStringExtra("relativeName") ?: "N/A"
            val relativeContact = intent.getStringExtra("relativeContact") ?: "N/A"

            // Set text fields
            textFullName.text = fullName
            textPatientNumber.text = "Patient #: $patientNumber"
            textAge.text = "Age: $age"
            textHealthCondition.text = "Condition: $healthCondition"
            textAddress.text = "Address: $address"
            textRelativeName.text = "Relative: $relativeName"
            textRelativeContact.text = "Contact: $relativeContact"

            // Load profile image using Glide
            if (!profileImageUri.isNullOrEmpty()) {
                Glide.with(this)
                    .load(Uri.parse(profileImageUri))
                    .placeholder(R.drawable.icusers)
                    .error(R.drawable.icusers)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageProfile)
            } else {
                imageProfile.setImageResource(R.drawable.icusers)
            }

        } catch (e: Exception) {
            Log.e("PersonalInformation", "Error loading patient information", e)
            Toast.makeText(this, "Failed to load patient information.", Toast.LENGTH_LONG).show()
        }
    }
}
