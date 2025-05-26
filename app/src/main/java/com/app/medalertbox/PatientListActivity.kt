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
                    } catch (e: Exception) {
                        Log.e("PatientListActivity", "Image load error", e)
                        Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PatientViewModel::class.java]
        setupRecyclerView()
        setupObservers()
        setupBackButtonForCaregiver()

        binding.fabAddPatient.setOnClickListener { showAddPatientDialog() }
    }

    private fun setupRecyclerView() {
        adapter = PatientAdapter(
            onDelete = {
                viewModel.delete(it)
                deletePatientFromFirestore(it.patientNumber)
            },
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
                val intent = Intent(this, ListOfMedicationsActivity::class.java).apply {
                    putExtra("patientId", patient.id)
                    putExtra("patientNumber", patient.patientNumber)
                    putExtra("patientName", "${patient.firstName} ${patient.lastName}")
                }
                startActivity(intent)
            }
        )
        binding.recyclerViewPatients.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPatients.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.allPatients.observe(this) { patientList ->
            adapter.submitList(patientList)
        }
    }

    private fun setupBackButtonForCaregiver() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val backButton = binding.btnBackToDashboard
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { doc ->
                    userRole = doc.getString("role")
                    if (userRole == "caregiver") {
                        backButton.visibility = View.VISIBLE
                        backButton.setOnClickListener {
                            startActivity(Intent(this, CaregiverDashboardActivity::class.java))
                            finish()
                        }
                    } else {
                        backButton.visibility = View.GONE
                    }
                }
                .addOnFailureListener {
                    Log.e("PatientListActivity", "Failed to fetch user role", it)
                }
        }
    }

    private fun showAddPatientDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_patient, null)

        val firstName = dialogView.findViewById<EditText>(R.id.editFirstName)
        val middleName = dialogView.findViewById<EditText>(R.id.editMiddleName)
        val lastName = dialogView.findViewById<EditText>(R.id.editLastName)
        val ageEdit = dialogView.findViewById<EditText>(R.id.editAge)
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
                    val ageStr = ageEdit.text.toString().trim()

                    if (first.isEmpty() || last.isEmpty()) {
                        Toast.makeText(this, "First and last name are required.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val parsedAge = ageStr.toIntOrNull()
                    if (parsedAge == null || parsedAge < 0) {
                        Toast.makeText(this, "Invalid age entered.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val currentList = viewModel.allPatients.value ?: emptyList()
                    val maxNumber = currentList.mapNotNull { it.patientNumber.toIntOrNull() }.maxOrNull() ?: 0
                    val newPatientNum = String.format("%03d", maxNumber + 1)

                    val patient = Patient(
                        firstName = first,
                        middleName = middleName.text.toString().trim(),
                        lastName = last,
                        age = parsedAge,
                        healthCondition = healthCondition.text.toString().trim(),
                        address = address.text.toString().trim(),
                        relativeName = relativeName.text.toString().trim(),
                        relativeContact = relativeContact.text.toString().trim(),
                        patientNumber = newPatientNum,
                        profileImageUri = selectedImageUri?.toString() ?: ""
                    )

                    viewModel.insert(patient)
                    savePatientToFirestore(patient)
                    dialog.dismiss()

                    selectedImageUri = null
                    currentImageView = null

                } catch (e: Exception) {
                    Log.e("PatientListActivity", "Error adding patient", e)
                    Toast.makeText(this, "Error adding patient.", Toast.LENGTH_LONG).show()
                }
            }
        }

        dialog.setOnDismissListener {
            selectedImageUri = null
            currentImageView = null
        }

        dialog.show()
    }

    private fun savePatientToFirestore(patient: Patient) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            db.collection("patients")
                .document(patient.patientNumber)
                .set(patient)
                .addOnSuccessListener {
                    Log.d("PatientListActivity", "Saved patient to Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e("PatientListActivity", "Failed to save patient", e)
                }
        }
    }

    private fun deletePatientFromFirestore(patientNumber: String) {
        FirebaseFirestore.getInstance()
            .collection("patients")
            .document(patientNumber)
            .delete()
            .addOnSuccessListener {
                Log.d("PatientListActivity", "Deleted patient from Firestore")
            }
            .addOnFailureListener {
                Log.e("PatientListActivity", "Failed to delete patient from Firestore", it)
            }
    }
}
