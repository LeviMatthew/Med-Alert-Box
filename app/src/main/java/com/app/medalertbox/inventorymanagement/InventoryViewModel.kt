package com.app.medalertbox.inventorymanagement

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InventoryRepository
    val allInventory: LiveData<List<Inventory>>
    val lowStockInventory: LiveData<List<Inventory>>

    private val firestore = FirebaseFirestore.getInstance()

    init {
        val inventoryDao = InventoryDatabase.getDatabase(application).inventoryDao()
        repository = InventoryRepository(inventoryDao)
        allInventory = repository.allInventory
        lowStockInventory = repository.lowStockInventory
    }

    fun insertInventory(inventory: Inventory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertInventory(inventory)
            upsertToFirestore(inventory)
        }
    }

    fun deleteInventory(inventory: Inventory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteInventory(inventory)
            deleteFromFirestore(inventory)
        }
    }

    private fun upsertToFirestore(inventory: Inventory) {
        val documentId = "${inventory.name}_${inventory.unitType}".replace(" ", "_")

        Log.d("UploadDebug", "Attempting to upload: ${inventory.name}")

        val inventoryMap = hashMapOf(
            "name" to inventory.name,
            "stockQuantity" to inventory.stockQuantity,
            "expirationDate" to inventory.expirationDate,
            "unitType" to inventory.unitType,
            "reorderLevel" to inventory.reorderLevel,
            "grams" to inventory.grams,
            "quantity" to inventory.quantity
        )

        // Upload to "medications"
        firestore.collection("medications")
            .document(documentId)
            .set(inventoryMap)
            .addOnSuccessListener {
                Log.d("Firestore", "Medication upserted: $documentId")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Medication upsert failed: $documentId", e)
            }

        // Upload to "inventory"
        firestore.collection("inventory")
            .document(documentId)
            .set(inventoryMap)
            .addOnSuccessListener {
                Log.d("Firestore", "Inventory upserted: $documentId")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Inventory upsert failed: $documentId", e)
            }
    }

    private fun deleteFromFirestore(inventory: Inventory) {
        val documentId = "${inventory.name}_${inventory.unitType}".replace(" ", "_")

        // Delete from "medications"
        firestore.collection("medications")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Deleted from medications: $documentId")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Delete failed from medications: $documentId", e)
            }

        // Delete from "inventory"
        firestore.collection("inventory")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Deleted from inventory: $documentId")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Delete failed from inventory: $documentId", e)
            }
    }
}
