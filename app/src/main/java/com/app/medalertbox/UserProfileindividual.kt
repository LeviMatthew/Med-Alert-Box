package com.app.medalertbox

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class UserProfileindividual : AppCompatActivity() {

    private lateinit var tvSavedInfo: TextView
    private lateinit var etName: EditText
    private lateinit var etContact: EditText
    private lateinit var etEmergency: EditText
    private lateinit var etMedicalHistory: EditText
    private lateinit var etAllergies: EditText
    private lateinit var spinnerCaregiver: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnHome: Button
    private lateinit var imgProfile: ImageView

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profileindividual)

        // Initialize views
        tvSavedInfo = findViewById(R.id.tvSavedInfo)
        etName = findViewById(R.id.etName)
        etContact = findViewById(R.id.etContact)
        etEmergency = findViewById(R.id.etEmergency)
        etMedicalHistory = findViewById(R.id.etMedicalHistory)
        etAllergies = findViewById(R.id.etAllergies)
        spinnerCaregiver = findViewById(R.id.spinnerCaregiver)
        btnSave = findViewById(R.id.btnSave)
        btnHome = findViewById(R.id.btnHome)
        imgProfile = findViewById(R.id.imgProfile)

        checkPermissions()

        // Populate Spinner (example values)
        val caregivers = arrayOf("Select Caregiver", "Alice", "Bob", "Charlie")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, caregivers)
        spinnerCaregiver.adapter = adapter

        loadProfileData()

        imgProfile.setOnClickListener {
            openGallery()
        }

        btnSave.setOnClickListener {
            saveProfileData()
        }

        btnHome.setOnClickListener {
            startActivity(Intent(this, SeniorDashboardActivity::class.java))
            finish()
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PICK_IMAGE_REQUEST)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                handleImageSelection(imageUri)
            }
        }
    }

    private fun handleImageSelection(imageUri: Uri) {
        try {
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            imgProfile.setImageBitmap(bitmap)
            saveProfilePicture(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveProfilePicture(bitmap: Bitmap) {
        try {
            val filename = "profile_image.png"
            val fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()

            val sharedPref = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("profilePicPath", filename)
                apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadProfileData() {
        val sharedPref = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", "") ?: ""
        val contact = sharedPref.getString("contact", "") ?: ""
        val emergency = sharedPref.getString("emergency", "") ?: ""
        val medical = sharedPref.getString("medical", "") ?: ""
        val allergies = sharedPref.getString("allergies", "") ?: ""
        val caregiver = sharedPref.getString("caregiver", "Select Caregiver") ?: ""
        val profilePicPath = sharedPref.getString("profilePicPath", null)

        val index = (spinnerCaregiver.adapter as ArrayAdapter<String>).getPosition(caregiver)
        spinnerCaregiver.setSelection(if (index >= 0) index else 0)

        etName.setText(name)
        etContact.setText(contact)
        etEmergency.setText(emergency)
        etMedicalHistory.setText(medical)
        etAllergies.setText(allergies)

        displaySavedInfo(name, contact, emergency, allergies)

        if (profilePicPath != null) {
            try {
                val fileInputStream = openFileInput(profilePicPath)
                val bitmap = BitmapFactory.decodeStream(fileInputStream)
                imgProfile.setImageBitmap(bitmap)
                fileInputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveProfileData() {
        val name = etName.text.toString().trim()
        val contact = etContact.text.toString().trim()
        val emergency = etEmergency.text.toString().trim()
        val medical = etMedicalHistory.text.toString().trim()
        val allergies = etAllergies.text.toString().trim()
        val caregiver = spinnerCaregiver.selectedItem.toString()

        val sharedPref = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("name", name)
            putString("contact", contact)
            putString("emergency", emergency)
            putString("medical", medical)
            putString("allergies", allergies)
            putString("caregiver", caregiver)
            apply()
        }

        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
        displaySavedInfo(name, contact, emergency, allergies)
    }

    private fun displaySavedInfo(name: String, contact: String, emergency: String, allergies: String) {
        tvSavedInfo.text = "Name: $name\nContact: $contact\nEmergency: $emergency\nAllergies: $allergies"
    }
}
