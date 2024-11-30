package br.com.cardapio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.cardapio.adapters.ItemAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListProductsActivity : AppCompatActivity() {

    private lateinit var itemAdapter: ItemAdapter
    private val items = mutableListOf<Item>()
    private val filteredItems = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_products)

        val cartButton: FloatingActionButton = findViewById(R.id.goToCart)
        cartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        itemAdapter = ItemAdapter(filteredItems)
        recyclerView.adapter = itemAdapter

        val categorySpinner: Spinner = findViewById(R.id.categorySpinner)
        setupCategorySpinner(categorySpinner)

        fetchItemsFromFirestore()

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list_products, menu)
        return true
    }

    private fun fetchItemsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("produtos")
            .get()
            .addOnSuccessListener { result ->
                items.clear()
                for (document in result) {
                    val dbitem = document.toObject(Item::class.java)
                    val item = Item(
                        document.id,
                        dbitem.name,
                        dbitem.description,
                        dbitem.picture,
                        dbitem.price,
                        dbitem.category
                    )
                    items.add(item)
                }
                applyCategoryFilter("Todos")
            }
            .addOnFailureListener { exception ->
                Log.e("ListItemsActivity", "Erro ao buscar itens: ", exception)
            }
    }

    private fun setupCategorySpinner(spinner: Spinner) {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedCategory = parent?.getItemAtPosition(position).toString()
                applyCategoryFilter(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                applyCategoryFilter("Todos")
            }
        }
    }

    private fun applyCategoryFilter(category: String) {
        filteredItems.clear()
        if (category == "Todos") {
            filteredItems.addAll(items)
        } else {
            val categoryKey = when (category) {
                "Comidas" -> "food"
                "Bebidas" -> "drinks"
                "Doces" -> "sweet"
                else -> ""
            }
            filteredItems.addAll(items.filter { it.category == categoryKey })
        }
        itemAdapter.notifyDataSetChanged()
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
        FirebaseAuth.getInstance().signOut()

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}