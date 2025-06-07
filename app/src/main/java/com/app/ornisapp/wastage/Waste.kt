package com.app.ornisapp.wastage

data class Waste(
    val itemName: String = "",
    val quantity: Int = 0,
    val pricePerUnit: Int = 0,
    val totalLoss: Int = 0,
    val reason: String = "",
    val date: String = "",
    val month: String = ""
)