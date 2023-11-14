package com.example.campuscafe.model

import java.io.Serializable

data class OrderDetails(
    var uid: String? = null,
    var username: String? = null,
    var total: String? = null,
    var foodNames: MutableList<String>? = null,
    var foodPrices: MutableList<String>? = null,
    var foodQuantities: MutableList<Int>? = null,
    var phoneNumber: String? = null,
    var orderAccepted: Boolean = false,
    var orderDispatched: Boolean = false,
    var orderCompleted: Boolean = false,
    var itemPushKey: String? = null,
    var currentTime: Long = 0
):Serializable