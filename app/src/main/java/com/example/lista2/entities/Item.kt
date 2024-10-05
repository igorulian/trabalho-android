package com.example.lista2

import java.io.Serializable

data class Item(
    val name: String,
    val quantity: Int,
    val unit: String,
    val category: String
) : Serializable
