package com.app.medalertbox.inventorymanagement

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "medications")
@TypeConverters(DateConverter::class)
data class Inventory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val stockQuantity: Int,
    val expirationDate: Long,
    val reorderLevel: Int
)
