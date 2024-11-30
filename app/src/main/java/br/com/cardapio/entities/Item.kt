package br.com.cardapio

import android.media.MediaRouter.RouteCategory


data class Item(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val picture: String = "",
    val price: Double = 0.0,
    val category: String? = "",
)
