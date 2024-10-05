package com.example.lista2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListItemsActivity : AppCompatActivity() {

    private lateinit var itemList: MutableList<Item>
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var listName: String
    private val sharedPrefsKey = "shared_prefs_items"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_items)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listName = intent.getStringExtra("item_name") ?: "Default List"
        toolbar.title = listName

        // Load the items for this specific list
        itemList = loadItems(listName)

        // Initialize the adapter
        itemAdapter = ItemAdapter(itemList) { position ->
            // Remove item on click
            itemList.removeAt(position)
            itemAdapter.notifyItemRemoved(position)
            saveItems(listName) // Save the updated list
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = itemAdapter

        val addListFab: FloatingActionButton = findViewById(R.id.addListFab)
        addListFab.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivityForResult(intent, ADD_ITEM_REQUEST_CODE)
        }

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
                itemAdapter.notifyItemInserted(itemList.size - 1)
                saveItems(listName) // Save the updated list
            }
        }
    }

    // Save items to SharedPreferences for a specific list
    private fun saveItems(listName: String) {
        val sharedPreferences = getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(itemList)
        editor.putString(listName, json)
        editor.apply()
    }

    // Load items from SharedPreferences for a specific list
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

    companion object {
        const val ADD_ITEM_REQUEST_CODE = 1
    }
}
