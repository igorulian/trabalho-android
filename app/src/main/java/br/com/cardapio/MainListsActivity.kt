package br.com.cardapio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class MainListsActivity : AppCompatActivity() {

    private lateinit var items: MutableList<ListItem>
    private lateinit var adapter: ListAdapter
    private lateinit var filteredItems: MutableList<ListItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_lists)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val addListFab: FloatingActionButton = findViewById(R.id.addListFab)
        addListFab.setOnClickListener {
            val intent = Intent(this, AddListActivity::class.java)
            startActivity(intent)
        }

        items = getSavedItems()
        filteredItems = items.toMutableList()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ListAdapter(this, filteredItems,
            { item ->
                val intent = Intent(this@MainListsActivity, ListItemsActivity::class.java)
                intent.putExtra("item_id", item.id.toString())
                intent.putExtra("item_name", item.name)
                intent.putExtra("item_image_uri", item.imageUri)
                startActivity(intent)
            },
            { item ->
                val intent = Intent(this@MainListsActivity, AddListActivity::class.java)
                intent.putExtra("item_id", item.id.toString())
                intent.putExtra("item_name", item.name)
                intent.putExtra("item_image_uri", item.imageUri)
                intent.putExtra("is_editing", true)
                startActivity(intent)
            }
        )

        recyclerView.adapter = adapter

        val newItem: ListItem? = intent.getParcelableExtra("new_item")
        val editedItem: ListItem? = intent.getParcelableExtra("edited_item")
        if (newItem != null) {
            addItem(newItem)
        } else if (editedItem != null) {
            updateItem(editedItem)
        }

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterList(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filterList(query: String) {
        filteredItems.clear()
        if (query.isEmpty()) {
            filteredItems.addAll(items)
        } else {
            filteredItems.addAll(items.filter {
                it.name.contains(query, ignoreCase = true)
            })
        }
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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
        filterList("")
        saveItems()
    }

    fun updateItem(item: ListItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item
            filterList("")
            saveItems()
        }
    }

    fun removeItem(item: ListItem) {
        items.remove(item)
        filterList("")
        saveItems()
    }
}
