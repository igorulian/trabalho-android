package com.example.lista2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class AddItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        // Set up the unit spinner
        val unitSpinner: Spinner = findViewById(R.id.unit_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.units_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            unitSpinner.adapter = adapter
        }

        // Set up the category spinner
        val categorySpinner: Spinner = findViewById(R.id.category_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }

        val addButton: Button = findViewById(R.id.add_button)
        addButton.setOnClickListener {
            val itemName = findViewById<EditText>(R.id.item_name).text.toString()
            val quantity = findViewById<EditText>(R.id.quantity).text.toString().toIntOrNull() ?: 0
            val unit = unitSpinner.selectedItem.toString()
            val category = categorySpinner.selectedItem.toString()

            val newItem = Item(itemName, quantity, unit, category)
            val resultIntent = Intent().apply {
                putExtra("new_item", newItem)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
