package com.example.lista2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(
    private val items: List<Item>,
    private val onDeleteClick: (Int) -> Unit,
    private val onEditClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onDeleteClick, onEditClick)
    }

    override fun getItemCount(): Int = items.size

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName: TextView = itemView.findViewById(R.id.itemNameTextView)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        private val editButton: Button = itemView.findViewById(R.id.editButton)

        fun bind(item: Item, onDeleteClick: (Int) -> Unit, onEditClick: (Item) -> Unit) {
            itemName.text = "${item.name} - ${item.quantity} ${item.unit} - ${item.category}"

            // Clique no botão "Excluir"
            deleteButton.setOnClickListener {
                onDeleteClick(adapterPosition)
            }

            // Clique no botão "Editar"
            editButton.setOnClickListener {
                onEditClick(item)
            }
        }
    }
}
