package com.app.medalertbox.patient

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.medalertbox.PatientProfileActivity
import com.app.medalertbox.R

class PatientAdapter(
    private val onDelete: (Patient) -> Unit,
    private val onImageBind: (String?, ImageView) -> Unit,
    private val onUploadClick: (Patient, ImageView) -> Unit,
    private val onItemClick: (Patient) -> Unit
) : ListAdapter<Patient, PatientAdapter.PatientViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = getItem(position)
        holder.bind(patient)

        // Optional item click
        holder.itemView.setOnClickListener {
            onItemClick(patient)
        }
    }

    inner class PatientViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val imgProfile: ImageView = view.findViewById(R.id.imgPatientProfile)
        private val txtNumber: TextView = view.findViewById(R.id.txtPatientNumber)
        private val btnDelete: ImageButton = view.findViewById(R.id.btnDeletePatient)
        private val btnPatientInfo: Button = view.findViewById(R.id.btnPatientInformation)

        fun bind(patient: Patient) {
            try {
                val fullName = buildString {
                    patient.firstName?.takeIf { it.isNotBlank() }?.let { append(it + " ") }
                    patient.middleName?.takeIf { it.isNotBlank() }?.let { append(it + " ") }
                    patient.lastName?.takeIf { it.isNotBlank() }?.let { append(it) }
                }.trim().ifEmpty { "Name not available" }

                btnPatientInfo.text = fullName
                txtNumber.text = "Patient #: ${patient.patientNumber.orEmpty()}"

                // Load image
                onImageBind(patient.profileImageUri, imgProfile)

                // ✅ Navigate to PatientProfileActivity with patientId included
                btnPatientInfo.setOnClickListener {
                    val context = view.context
                    val intent = Intent(context, PatientProfileActivity::class.java).apply {
                        putExtra("patientId", patient.id) // ✅ Required for downstream activities
                        putExtra("firstName", patient.firstName)
                        putExtra("middleName", patient.middleName)
                        putExtra("lastName", patient.lastName)
                        putExtra("patientNumber", patient.patientNumber)
                        putExtra("age", patient.age)
                        putExtra("healthCondition", patient.healthCondition)
                        putExtra("address", patient.address)
                        putExtra("relativeName", patient.relativeName)
                        putExtra("relativeContact", patient.relativeContact)
                        putExtra("profileImageUri", patient.profileImageUri)
                    }
                    context.startActivity(intent)
                }

                // Delete action
                btnDelete.setOnClickListener {
                    onDelete(patient)
                }

                // Image upload/change
                imgProfile.setOnClickListener {
                    onUploadClick(patient, imgProfile)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(view.context, "Error displaying patient data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Patient>() {
            override fun areItemsTheSame(oldItem: Patient, newItem: Patient): Boolean {
                return oldItem.patientNumber == newItem.patientNumber
            }

            override fun areContentsTheSame(oldItem: Patient, newItem: Patient): Boolean {
                return oldItem == newItem
            }
        }
    }
}
