package com.app.medalertbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.medalertbox.databinding.ActivityMedicationListBinding

class MedicationListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMedicationListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("medicationName") ?: "N/A"
        val time = intent.getStringExtra("time") ?: "N/A"
        val date = intent.getStringExtra("date") ?: "N/A"

        binding.medicationNameText.text = "Medication: $name"
        binding.medicationTimeText.text = "Time: $time"
        binding.medicationDateText.text = "Date: $date"
    }
}
