package com.example.lista2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListItemsActivity : AppCompatActivity() {

    private lateinit var itemList: MutableList<Item>
    private lateinit var filteredItemList: MutableList<Item>
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var listName: String
    private val sharedPrefsKey = "shared_prefs_items"

    // Variável para armazenar o nome original do item que está sendo editado
    private var originalItemName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_items)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listName = intent.getStringExtra("item_name") ?: "Default List"
        toolbar.title = listName

        // Carregar os itens da lista específica
        itemList = loadItems(listName)
        filteredItemList = itemList.toMutableList()

        // Inicializar o adaptador
        itemAdapter = ItemAdapter(filteredItemList, { position ->
            // Clique em "Excluir"
            val item = filteredItemList[position]
            itemList.remove(item)
            filteredItemList.removeAt(position)
            itemAdapter.notifyItemRemoved(position)
            saveItems(listName)
        }, { item ->
            // Clique em "Editar"
            originalItemName = item.name // Armazena o nome original do item antes de editar
            val intent = Intent(this, AddItemActivity::class.java)
            intent.putExtra("item_name", item.name)
            intent.putExtra("item_quantity", item.quantity)
            intent.putExtra("item_unit", item.unit)
            intent.putExtra("item_category", item.category)
            intent.putExtra("is_editing", true)
            startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE)
        })

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = itemAdapter

        val addListFab: FloatingActionButton = findViewById(R.id.addListFab)
        addListFab.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivityForResult(intent, ADD_ITEM_REQUEST_CODE)
        }

        // Configurar a busca
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterList(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ITEM_REQUEST_CODE && resultCode == RESULT_OK) {
            val newItem = data?.getSerializableExtra("new_item") as? Item
            newItem?.let {
                itemList.add(it)
                filterList("")
                saveItems(listName)
            }
        } else if (requestCode == EDIT_ITEM_REQUEST_CODE && resultCode == RESULT_OK) {
            val editedItem = data?.getSerializableExtra("edited_item") as? Item
            editedItem?.let {
                updateItem(it)
                filterList("")
                saveItems(listName)
            }
        }
    }

    private fun filterList(query: String) {
        filteredItemList.clear()
        if (query.isEmpty()) {
            filteredItemList.addAll(itemList)
        } else {
            filteredItemList.addAll(itemList.filter {
                it.name.contains(query, ignoreCase = true)
            })
        }
        itemAdapter.notifyDataSetChanged()
    }

    private fun saveItems(listName: String) {
        val sharedPreferences = getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(itemList)
        editor.putString(listName, json)
        editor.apply()
    }

    private fun loadItems(listName: String): MutableList<Item> {
        val sharedPreferences = getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(listName, null)
        val type = object : TypeToken<MutableList<Item>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    private fun updateItem(item: Item) {
        val index = itemList.indexOfFirst { it.name == originalItemName }
        if (index != -1) {
            itemList[index] = item
            filteredItemList[index] = item
            itemAdapter.notifyItemChanged(index)
        }
    }

    companion object {
        const val ADD_ITEM_REQUEST_CODE = 1
        const val EDIT_ITEM_REQUEST_CODE = 2
    }
}
