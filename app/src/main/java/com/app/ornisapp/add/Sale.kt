package com.app.ornisapp.add

data class Sale(
    val itemName: String = "",
    val quantity: Int = 0,
    val pricePerUnit: Int = 0,
    val totalAmount: Int = 0,
    val paymentMode: String = "",
    val remarks: String = ""
)
