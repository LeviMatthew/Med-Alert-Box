package com.app.medalertbox.inventorymanagement

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(medication: Inventory)

    @Delete
    suspend fun deleteInventory(inventory: Inventory)

    @Query("SELECT * FROM medications ORDER BY name ASC")
    fun getAllInventory(): LiveData<List<Inventory>>

    @Query("SELECT * FROM medications WHERE stockQuantity <= reorderLevel")
    fun getLowStockInventory(): LiveData<List<Inventory>>
}
