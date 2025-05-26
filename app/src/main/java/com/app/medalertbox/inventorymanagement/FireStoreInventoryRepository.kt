package com.app.medalertbox.inventorymanagement

import com.app.medalertbox.inventorymanagement.Inventory
import com.google.firebase.firestore.FirebaseFirestore

class FireStoreInventoryRepository {

    private val db = FirebaseFirestore.getInstance()

    fun addMedicationToPatient(patientNumber: String, medication: Inventory, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val medMap = mapOf(
            "name" to medication.name,
            "stockQuantity" to medication.stockQuantity,
            "expirationDate" to medication.expirationDate,
            "unitType" to medication.unitType,
            "reorderLevel" to medication.reorderLevel,
            "grams" to medication.grams,
            "quantity" to medication.quantity
        )

        db.collection("patients")
            .document(patientNumber)
            .collection("medications")
            .add(medMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}
