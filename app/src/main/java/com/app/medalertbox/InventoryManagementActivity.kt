package com.app.medalertbox

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.medalertbox.databinding.ActivityInventoryManagementBinding
import com.app.medalertbox.inventorymanagement.Inventory
import com.app.medalertbox.inventorymanagement.InventoryAdapter
import com.app.medalertbox.inventorymanagement.InventoryViewModel

class InventoryManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryManagementBinding
    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: InventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInventoryManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)
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

        binding.btnHome.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showAddMedicationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Medication")

        val input = EditText(this)
        input.hint = "Enter medication name"
        builder.setView(input)

        builder.setPositiveButton("Add") { _, _ ->
            val medicationName = input.text.toString()
            if (medicationName.isNotEmpty()) {
                val newInventory = Inventory(0, medicationName, 1, System.currentTimeMillis(), 5)
                viewModel.insertInventory(newInventory)
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
} 
