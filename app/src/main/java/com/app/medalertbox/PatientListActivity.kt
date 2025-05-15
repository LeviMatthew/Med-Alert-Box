package com.app.medalertbox

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.medalertbox.databinding.ActivityPatientListBinding
import com.app.medalertbox.patient.Patient
import com.app.medalertbox.patient.PatientAdapter
import com.app.medalertbox.patient.PatientViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PatientListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientListBinding
    private lateinit var viewModel: PatientViewModel
    private lateinit var adapter: PatientAdapter
    private var selectedImageUri: Uri? = null
    private var currentImageView: ImageView? = null
    private var userRole: String? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                    try {
                        currentImageView?.let { imageView ->
                            Glide.with(this)
                                .load(uri)
                                .placeholder(R.drawable.icusers)
                                .error(R.drawable.icusers)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView)
                        }
                    } catch (e: SecurityException) {
                        Log.e("PatientListActivity", "Image load failed - permission denied", e)
                        Toast.makeText(this, "Failed to load image: Permission denied", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Log.e("PatientListActivity", "Error loading image", e)
                        Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityPatientListBinding.inflate(layoutInflater)
            setContentView(binding.root)

            viewModel = ViewModelProvider(this)[PatientViewModel::class.java]

            adapter = PatientAdapter(
                onDelete = { patient -> viewModel.delete(patient) },
                onImageBind = { uri, imageView ->
                    try {
                        if (!uri.isNullOrEmpty()) {
                            Glide.with(imageView.context)
                                .load(Uri.parse(uri))
                                .placeholder(R.drawable.icusers)
                                .error(R.drawable.icusers)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView)
                        } else {
                            imageView.setImageResource(R.drawable.icusers)
                        }
                    } catch (e: Exception) {
                        Log.e("PatientListActivity", "Image bind failed", e)
                        imageView.setImageResource(R.drawable.icusers)
                    }
                },
                onUploadClick = { _, _ -> },
                onItemClick = { patient ->
                    val intent = Intent(this, PersonalInformationActivity::class.java).apply {
                        putExtra("profileImageUri", patient.profileImageUri)
                        putExtra("fullName", "${patient.firstName} ${patient.middleName} ${patient.lastName}".trim())
                        putExtra("patientNumber", patient.patientNumber)
                        putExtra("healthCondition", patient.healthCondition)
                        putExtra("age", patient.age.toString()) // ✅ Pass age here
                    }
                    startActivity(intent)
                }
            )

            binding.recyclerViewPatients.layoutManager = LinearLayoutManager(this)
            binding.recyclerViewPatients.adapter = adapter

            viewModel.allPatients.observe(this) {
                adapter.submitList(it)
            }

            binding.fabAddPatient.setOnClickListener {
                showAddPatientDialog()
            }

            setupBackButtonForCaregiver()

        } catch (e: Exception) {
            Log.e("PatientListActivity", "onCreate crash", e)
            Toast.makeText(this, "An error occurred while loading patient data.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupBackButtonForCaregiver() {
        val backButton = binding.btnBackToDashboard
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    userRole = document.getString("role")
                    if (userRole == "caregiver") {
                        backButton.visibility = View.VISIBLE
                        backButton.setOnClickListener {
                            val intent = Intent(this, CaregiverDashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        backButton.visibility = View.GONE
                    }
                }
                .addOnFailureListener {
                    Log.e("PatientListActivity", "Failed to retrieve user role", it)
                }
        }
    }

    private fun showAddPatientDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_patient, null)

        val firstName = dialogView.findViewById<EditText>(R.id.editFirstName)
        val middleName = dialogView.findViewById<EditText>(R.id.editMiddleName)
        val lastName = dialogView.findViewById<EditText>(R.id.editLastName)
        val number = dialogView.findViewById<EditText>(R.id.editPatientNumber)
        val age = dialogView.findViewById<EditText>(R.id.editAge)
        val healthCondition = dialogView.findViewById<EditText>(R.id.editHealthCondition)
        val address = dialogView.findViewById<EditText>(R.id.editAddress)
        val relativeName = dialogView.findViewById<EditText>(R.id.editRelativeName)
        val relativeContact = dialogView.findViewById<EditText>(R.id.editRelativeContact)
        val imgUpload = dialogView.findViewById<ImageView>(R.id.imgPatientProfile)

        imgUpload.setOnClickListener {
            currentImageView = imgUpload
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Patient")
            .setView(dialogView)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel") { _, _ ->
                selectedImageUri = null
                currentImageView = null
            }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                try {
                    val first = firstName.text.toString().trim()
                    val last = lastName.text.toString().trim()
                    val patientNum = number.text.toString().trim()

                    if (first.isEmpty() || last.isEmpty() || patientNum.isEmpty()) {
                        Toast.makeText(this, "First name, last name, and patient number are required.", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }

                    val ageInput = age.text.toString().trim()
                    val parsedAge = ageInput.toIntOrNull()
                    if (parsedAge == null || parsedAge < 0) {
                        Toast.makeText(this, "Invalid age. Please enter a valid number.", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }

                    val patient = Patient(
                        firstName = first,
                        middleName = middleName.text.toString().trim(),
                        lastName = last,
                        patientNumber = patientNum,
                        age = parsedAge,
                        healthCondition = healthCondition.text.toString().trim(),
                        address = address.text.toString().trim(),
                        relativeName = relativeName.text.toString().trim(),
                        relativeContact = relativeContact.text.toString().trim(),
                        profileImageUri = selectedImageUri?.toString() ?: ""
                    )

                    viewModel.insert(patient)
                    selectedImageUri = null
                    currentImageView = null
                    dialog.dismiss()

                } catch (e: Exception) {
                    Log.e("PatientListActivity", "Error adding patient", e)
                    Toast.makeText(this, "Failed to add patient. Check all inputs.", Toast.LENGTH_LONG).show()
                }
            }
        }

        dialog.setOnDismissListener {
            selectedImageUri = null
            currentImageView = null
        }

        dialog.show()
    }
}
