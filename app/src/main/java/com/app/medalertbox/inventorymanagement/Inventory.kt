package com.app.medalertbox.inventorymanagement

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "medications")
@TypeConverters(DateConverter::class)
data class Inventory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val stockQuantity: Int,
    val expirationDate: Long,
    val unitType: String,
    val reorderLevel: Int,
    val grams: Int,
    val quantity: Int
) {
constructor() : this(0, "", 0, 0L, "", 0, 0, 0) // Needed for Firestore deserialization
}

