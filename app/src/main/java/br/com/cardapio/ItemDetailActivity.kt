package br.com.cardapio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ItemDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val itemId = intent.getStringExtra("item_id")
        val itemName = intent.getStringExtra("item_name")
        val itemDescription = intent.getStringExtra("item_description")
        val itemPrice = intent.getDoubleExtra("item_price", 0.0)
        val itemPicture = intent.getStringExtra("item_picture")

        val nameTextView: TextView = findViewById(R.id.itemName)
        val descriptionTextView: TextView = findViewById(R.id.itemDescription)
        val priceTextView: TextView = findViewById(R.id.itemPrice)
        val pictureImageView: ImageView = findViewById(R.id.itemPicture)
        val addToCartButton: Button = findViewById(R.id.addToCartButton)

        nameTextView.text = itemName
        descriptionTextView.text = itemDescription
        priceTextView.text = "R$ $itemPrice"
        Glide.with(this).load(itemPicture).into(pictureImageView)


        addToCartButton.setOnClickListener {

            if (itemId.isNullOrEmpty()) {
                showToast("Erro: ID do item inválido.")
                return@setOnClickListener
            }

            if (itemName.isNullOrEmpty()) {
                showToast("Erro: Nome do item inválido.")
                return@setOnClickListener
            }

            if (itemDescription.isNullOrEmpty()) {
                showToast("Erro: Descrição do item inválida.")
                return@setOnClickListener
            }

            if (itemPrice < 0) {
                showToast("Erro: Preço do item inválido.")
                return@setOnClickListener
            }

            if (itemPicture.isNullOrEmpty()) {
                showToast("Erro: Imagem do item inválida.")
                return@setOnClickListener
            }

            val item = Item(
                itemId,
                itemName,
                itemDescription,
                itemPicture,
                itemPrice
            )

            addToCart(item)
        }
    }

    private fun addToCart(item: Item) {

        val sharedPreferences = getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
        val gson = Gson()

        val cartJson = sharedPreferences.getString("cart_items", null)
        val cartItems: MutableList<Item> = if (cartJson != null) {
            val type = object : TypeToken<MutableList<Item>>() {}.type
            gson.fromJson(cartJson, type)
        } else {
            mutableListOf()
        }

        cartItems.add(item)
        val editor = sharedPreferences.edit()
        editor.putString("cart_items", gson.toJson(cartItems))
        editor.apply()

        showToast("Item adicionado ao carrinho!")

        onBackPressed()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}