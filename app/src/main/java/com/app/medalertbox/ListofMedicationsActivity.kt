package com.app.medalertbox

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.medalertbox.databinding.ActivityListOfMedicationsBinding
import com.app.medalertbox.inventorymanagement.FireStoreInventoryRepository
import com.app.medalertbox.inventorymanagement.Inventory
import com.app.medalertbox.inventorymanagement.InventoryAdapter
import com.app.medalertbox.inventorymanagement.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.*

class ListOfMedicationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListOfMedicationsBinding
    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: InventoryAdapter
    private lateinit var firestoreRepo: FireStoreInventoryRepository

    private var patientNumber: String = "" // You should pass this via intent if applicable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOfMedicationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get patient number passed from previous activity (optional)
        patientNumber = intent.getStringExtra("patientNumber") ?: ""

        viewModel = ViewModelProvider(this)[InventoryViewModel::class.java]
        firestoreRepo = FireStoreInventoryRepository()

        adapter = InventoryAdapter(
            onUpdate = { inventory -> viewModel.insertInventory(inventory) },
            onDelete = { inventory -> viewModel.deleteInventory(inventory) }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.allInventory.observe(this) { medications ->
            adapter.submitList(medications)
        }

        binding.fabAddMedication.setOnClickListener {
            showAddMedicationDialog()
        }
    }

    private fun showAddMedicationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_medication, null)

        val txtMedicationName = dialogView.findViewById<EditText>(R.id.txtMedicationName)
        val spinnerForm = dialogView.findViewById<Spinner>(R.id.spinnerForm)
        val editGrams = dialogView.findViewById<EditText>(R.id.editGrams)
        val txtExpiration = dialogView.findViewById<TextView>(R.id.txtExpiration)
        val txtStockLevel = dialogView.findViewById<TextView>(R.id.txtStockLevel)
        val btnIncrease = dialogView.findViewById<ImageButton>(R.id.btnIncreaseStock)
        val btnDecrease = dialogView.findViewById<ImageButton>(R.id.btnDecreaseStock)

        var stockCount = 1
        txtStockLevel.text = "Stock: $stockCount"

        val forms = listOf("Tablet", "Capsule")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, forms)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerForm.adapter = adapterSpinner

        val calendar = Calendar.getInstance()
        var selectedExpirationTime = calendar.timeInMillis

        txtExpiration.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    selectedExpirationTime = calendar.timeInMillis
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    txtExpiration.text = sdf.format(Date(selectedExpirationTime))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnIncrease.setOnClickListener {
            stockCount++
            txtStockLevel.text = "Stock: $stockCount"
        }

        btnDecrease.setOnClickListener {
            if (stockCount > 1) {
                stockCount--
                txtStockLevel.text = "Stock: $stockCount"
            }
        }

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setTitle("Add Medication")
        builder.setPositiveButton("Add") { _, _ ->
            val name = txtMedicationName.text.toString().trim()
            val form = spinnerForm.selectedItem?.toString() ?: ""
            val grams = editGrams.text.toString().toIntOrNull()

            if (name.isNotEmpty() && grams != null && grams > 0 && form.isNotEmpty()) {
                val inventoryItem = Inventory(
                    id = 0,
                    name = "$name ($form)",
                    stockQuantity = stockCount,
                    expirationDate = selectedExpirationTime,
                    unitType = form,
                    reorderLevel = 5,
                    grams = grams,
                    quantity = stockCount
                )

                // Save to local Room database
                viewModel.insertInventory(inventoryItem)

                // Also save to Firestore if patient number is available
                if (patientNumber.isNotEmpty()) {
                    firestoreRepo.addMedicationToPatient(
                        patientNumber,
                        inventoryItem,
                        onSuccess = {
                            Toast.makeText(this, "Saved to Firestore", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = { e ->
                            Toast.makeText(this, "Firestore Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                }

            } else {
                Toast.makeText(this, "Enter valid name, form and grams > 0", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}
