package br.com.cardapio

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
            addToCart(itemName, itemPrice, itemPicture)
        }
    }

    private fun addToCart(name: String?, price: Double, picture: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val cartItem = hashMapOf(
                "name" to name,
                "price" to price,
                "picture" to picture,
                "user_id" to user.uid
            )
            db.collection("cart")
                .add(cartItem)
                .addOnSuccessListener {
                    Toast.makeText(this, "Item adicionado ao carrinho!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao adicionar ao carrinho: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
        }
    }
}