package com.example.campuscafe.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.ArrayList

data class OrderDetails(
    var uid: String? = null,
    var username: String? = null,
    var total: String? = null,
    var foodNames: MutableList<String>? = null,
    var foodPrices: MutableList<String>? = null,
    var foodQuantities: MutableList<Int>? = null,
    var phoneNumber: String? = null,
    var orderAccepted: Boolean = false,
    var paymentReceived: Boolean = false,
    var itemPushKey: String? = null,
    var currentTime: Long = 0
):Serializable

