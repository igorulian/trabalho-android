package com.example.lista2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class MainListsActivity : AppCompatActivity() {

    private lateinit var items: MutableList<ListItem>
    private lateinit var adapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_lists)

        val addListFab: FloatingActionButton = findViewById(R.id.addListFab)
        addListFab.setOnClickListener {
            val intent = Intent(this, AddListActivity::class.java)
            startActivity(intent)
        }

        // Recuperar itens salvos no SharedPreferences
        items = getSavedItems()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ListAdapter(this, items) { item ->
            val intent = Intent(this@MainListsActivity, ListItemsActivity::class.java)
            intent.putExtra("item_id", item.id.toString())
            intent.putExtra("item_name", item.name)
            intent.putExtra("item_image_uri", item.imageUri)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        val newItem: ListItem? = intent.getParcelableExtra("new_item")
        if (newItem != null) {
            addItem(newItem)
        }
    }

    private fun getSavedItems(): MutableList<ListItem> {
        val sharedPreferences = getSharedPreferences("list_prefs", Context.MODE_PRIVATE)
        val jsonArrayString = sharedPreferences.getString("items", "[]")
        val jsonArray = JSONArray(jsonArrayString)
        val itemList = mutableListOf<ListItem>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val id = UUID.fromString(jsonObject.getString("id"))
            val name = jsonObject.getString("name")
            val imageUri = jsonObject.optString("imageUri", null)
            itemList.add(ListItem(id, name, imageUri))
        }
        return itemList
    }

    fun saveItems() {
        val sharedPreferences = getSharedPreferences("list_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val jsonArray = JSONArray()
        for (item in items) {
            val jsonObject = JSONObject()
            jsonObject.put("id", item.id.toString())
            jsonObject.put("name", item.name)
            jsonObject.put("imageUri", item.imageUri)
            jsonArray.put(jsonObject)
        }
        editor.putString("items", jsonArray.toString())
        editor.apply()
    }

    fun addItem(item: ListItem) {
        items.add(item)
        adapter.notifyDataSetChanged()
        saveItems()
    }

    fun removeItem(item: ListItem) {
        items.remove(item)
        adapter.notifyDataSetChanged()
        saveItems()
    }
}
