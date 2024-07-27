package com.mobdeve.s11.mco2

data class Transaction(
    var amount: String? = null,
    var category: String? = null,
    var name: String? = null,
    var date: String? = null,
    var imageUrl: String? = null,
    var time: String? = null
) {
    val displayDate: String?
        get() = date?.substringBefore("-") // Extracts the date part before the first space
}


