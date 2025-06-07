package com.app.ornisapp.purchase

data class Purchase(
    val itemName: String = "",
    val quantity: Int = 0,
    val pricePerUnit: Int = 0,
    val totalAmount: Int = 0,
    val paymentMode: String = "",
    val date: String = "",
    val month: String = ""
)