package br.com.cardapio

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.util.UUID

class AddListActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private var isEditing = false
    private var editingItemId: UUID? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val listNameEditText: EditText = findViewById(R.id.listNameEditText)
        val selectImageButton: Button = findViewById(R.id.selectImageButton)
        val selectedImageView: ImageView = findViewById(R.id.selectedImageView)
        val addListButton: Button = findViewById(R.id.addListButton)

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1000)
        }

        isEditing = intent.getBooleanExtra("is_editing", false)
        if (isEditing) {
            listNameEditText.setText(intent.getStringExtra("item_name"))
            val imageUriString = intent.getStringExtra("item_image_uri")
            selectedImageUri = if (imageUriString != null) Uri.parse(imageUriString) else null
            selectedImageView.setImageURI(selectedImageUri)
            editingItemId = UUID.fromString(intent.getStringExtra("item_id"))
            addListButton.text = "Salvar"
        }

        addListButton.setOnClickListener {
            val listName = listNameEditText.text.toString().trim()
            if (validateInputs(listNameEditText, listName)) {
                val newItem = ListItem(
                    id = editingItemId ?: UUID.randomUUID(),
                    name = listName,
                    imageUri = selectedImageUri?.toString()
                )

                val mainActivityIntent = Intent(this, MainListsActivity::class.java)
                if (isEditing) {
                    mainActivityIntent.putExtra("edited_item", newItem)
                } else {
                    mainActivityIntent.putExtra("new_item", newItem)
                }
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

    private fun validateInputs(listNameEditText: EditText, listName: String): Boolean {
        var isValid = true
        if (listName.isEmpty()) {
            listNameEditText.error = "O nome da lista é obrigatório"
            isValid = false
        }
        if (selectedImageUri == null) {
            Toast.makeText(this, "A seleção de uma imagem é obrigatória", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        return isValid
    }
}
