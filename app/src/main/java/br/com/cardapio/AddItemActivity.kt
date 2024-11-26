package br.com.cardapio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class AddItemActivity : AppCompatActivity() {
    private var isEditing = false
    private var originalItemName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val itemNameEditText = findViewById<EditText>(R.id.item_name)
        val quantityEditText = findViewById<EditText>(R.id.quantity)
        val unitSpinner: Spinner = findViewById(R.id.unit_spinner)
        val categorySpinner: Spinner = findViewById(R.id.category_spinner)
        val addButton: Button = findViewById(R.id.add_button)

        ArrayAdapter.createFromResource(
            this,
            R.array.units_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            unitSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }

        isEditing = intent.getBooleanExtra("is_editing", false)
        if (isEditing) {
            val itemName = intent.getStringExtra("item_name")
            val itemQuantity = intent.getIntExtra("item_quantity", 0)
            val itemUnit = intent.getStringExtra("item_unit")
            val itemCategory = intent.getStringExtra("item_category")

            originalItemName = itemName

            itemNameEditText.setText(itemName)
            quantityEditText.setText(itemQuantity.toString())
            unitSpinner.setSelection(resources.getStringArray(R.array.units_array).indexOf(itemUnit))
            categorySpinner.setSelection(resources.getStringArray(R.array.category_array).indexOf(itemCategory))

            addButton.text = "Salvar"
        }

        addButton.setOnClickListener {
            val itemName = itemNameEditText.text.toString().trim()
            val quantity = quantityEditText.text.toString().toIntOrNull()
            val unit = unitSpinner.selectedItem.toString()
            val category = categorySpinner.selectedItem.toString()

            if (validateInputs(itemNameEditText, quantityEditText, itemName, quantity, unit, category)) {
                val newItem = Item(itemName, quantity!!, unit, category)
                val resultIntent = Intent().apply {
                    putExtra(if (isEditing) "edited_item" else "new_item", newItem)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun validateInputs(
        itemNameEditText: EditText,
        quantityEditText: EditText,
        itemName: String,
        quantity: Int?,
        unit: String,
        category: String
    ): Boolean {
        var isValid = true

        if (itemName.isEmpty()) {
            itemNameEditText.error = "O nome do item é obrigatório"
            isValid = false
        }

        if (quantity == null || quantity <= 0) {
            quantityEditText.error = "Insira uma quantidade válida maior que zero"
            isValid = false
        }

        if (unit.isEmpty() || unit == "Selecione a unidade") {
            Toast.makeText(this, "Selecione uma unidade válida", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (category.isEmpty() || category == "Selecione a categoria") {
            Toast.makeText(this, "Selecione uma categoria válida", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }
}
