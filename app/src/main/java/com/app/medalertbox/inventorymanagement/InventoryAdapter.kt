package com.app.medalertbox.inventorymanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.medalertbox.R

class InventoryAdapter(private val onUpdate: (Inventory) -> Unit, private val onDelete: (Inventory) -> Unit) :
    ListAdapter<Inventory, InventoryAdapter.InventoryViewHolder>(InventoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val inventory = getItem(position)
        holder.bind(inventory, onUpdate, onDelete)
    }

    class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtName: TextView = itemView.findViewById(R.id.txtMedicationName)
        private val txtStock: TextView = itemView.findViewById(R.id.txtStockLevel)
        private val btnIncrease: Button = itemView.findViewById(R.id.btnIncreaseStock)
        private val btnDecrease: Button = itemView.findViewById(R.id.btnDecreaseStock)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(inventory: Inventory, onUpdate: (Inventory) -> Unit, onDelete: (Inventory) -> Unit) {
            txtName.text = inventory.name
            txtStock.text = "Stock: ${inventory.stockQuantity}"

            btnIncrease.setOnClickListener {
                val updatedInventory = inventory.copy(stockQuantity = inventory.stockQuantity + 1)
                onUpdate(updatedInventory)
            }

            btnDecrease.setOnClickListener {
                if (inventory.stockQuantity > 0) {
                    val updatedInventory = inventory.copy(stockQuantity = inventory.stockQuantity - 1)
                    onUpdate(updatedInventory)
                }
            }

            btnDelete.setOnClickListener {
                onDelete(inventory)
            }
        }
    }
}

class InventoryDiffCallback : DiffUtil.ItemCallback<Inventory>() {
    override fun areItemsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
        return oldItem == newItem
    }
}

