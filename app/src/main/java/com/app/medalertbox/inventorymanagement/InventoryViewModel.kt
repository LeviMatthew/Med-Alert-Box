package com.app.medalertbox.inventorymanagement

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: InventoryRepository
    val allInventory: LiveData<List<Inventory>>
    val lowStockInventory: LiveData<List<Inventory>>

    init {
        val inventoryDao = InventoryDatabase.getDatabase(application).inventoryDao()
        repository = InventoryRepository(inventoryDao)
        allInventory = repository.allInventory
        lowStockInventory = repository.lowStockInventory
    }

    fun insertInventory(inventory: Inventory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertInventory(inventory)
        }
    }

    fun deleteInventory(inventory: Inventory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteInventory(inventory)
        }
    }
}
