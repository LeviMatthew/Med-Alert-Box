package com.app.medalertbox.inventorymanagement

import androidx.lifecycle.LiveData

class InventoryRepository(private val inventoryDao: InventoryDao) {
    val allInventory: LiveData<List<Inventory>> = inventoryDao.getAllInventory()
    val lowStockInventory: LiveData<List<Inventory>> = inventoryDao.getLowStockInventory()

    suspend fun insertInventory(inventory: Inventory) {
        inventoryDao.insertInventory(inventory)
    }

    suspend fun deleteInventory(inventory: Inventory) {
        inventoryDao.deleteInventory(inventory)
    }
}
