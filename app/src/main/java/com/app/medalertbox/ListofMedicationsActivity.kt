package com.app.medalertbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.medalertbox.databinding.ActivityListOfMedicationsBinding
import com.app.medalertbox.inventorymanagement.InventoryAdapter
import com.app.medalertbox.inventorymanagement.InventoryViewModel

class ListOfMedicationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListOfMedicationsBinding
    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: InventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOfMedicationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[InventoryViewModel::class.java]

        adapter = InventoryAdapter(
            onUpdate = { /* Optional: disable updating here if view-only */ },
            onDelete = { /* Optional: disable deleting here if view-only */ }
        )

        binding.recyclerViewMedications.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMedications.adapter = adapter

        viewModel.allInventory.observe(this) { medications ->
            adapter.submitList(medications)
        }
    }
}
