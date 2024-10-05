package com.example.lista2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class AddListActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val listNameEditText: EditText = findViewById(R.id.listNameEditText)
        val selectImageButton: Button = findViewById(R.id.selectImageButton)
        val selectedImageView: ImageView = findViewById(R.id.selectedImageView)
        val addListButton: Button = findViewById(R.id.addListButton)

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1000)
        }

        addListButton.setOnClickListener {
            val listName = listNameEditText.text.toString()
            if (listName.isNotEmpty()) {
                val newItem = ListItem(
                    name = listName,
                    imageUri = selectedImageUri?.toString()
                )

                val mainActivityIntent = Intent(this, MainListsActivity::class.java)
                mainActivityIntent.putExtra("new_item", newItem)
                startActivity(mainActivityIntent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            val selectedImageView: ImageView = findViewById(R.id.selectedImageView)
            selectedImageView.setImageURI(selectedImageUri)
        }
    }
}
