package br.com.cardapio

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartActivity : AppCompatActivity() {

    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadCartItems()

        cartAdapter = CartAdapter(cartItems) { itemToRemove ->
            removeItemFromCart(itemToRemove)
        }
        recyclerView.adapter = cartAdapter
    }

    private fun loadCartItems() {
        val sharedPreferences = getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
        val gson = Gson()

        val cartJson = sharedPreferences.getString("cart_items", null)
        if (cartJson != null) {
            val type = object : TypeToken<MutableList<Item>>() {}.type
            val items: MutableList<Item> = gson.fromJson(cartJson, type)
            cartItems.clear()
            cartItems.addAll(items)
        } else {
            Toast.makeText(this, "Carrinho está vazio", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeItemFromCart(item: Item) {
        cartItems.remove(item)

        val sharedPreferences = getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        editor.putString("cart_items", gson.toJson(cartItems))
        editor.apply()

        cartAdapter.updateItems(cartItems)
        loadCartItems()

        Toast.makeText(this, "Item removido do carrinho", Toast.LENGTH_SHORT).show()
    }

}