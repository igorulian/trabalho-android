package br.com.cardapio

import java.io.Serializable

data class Item(
    val name: String,
    val quantity: Int,
    val unit: String,
    val category: String
) : Serializable
