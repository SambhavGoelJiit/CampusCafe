package com.example.campuscafe.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.ArrayList

class OrderDetails(): Serializable {
    private var uid: String? = null
    private var username: String? = null
    var foodNames: MutableList<String>? = null
    var foodPrices: MutableList<String>? = null
    var foodQuantities: MutableList<Int>? = null
    private var phoneNumber: String? = null
    private var orderAccepted: Boolean = false
    private var paymentReceived: Boolean = false
    var itemPushKey: String? = null
    private var currentTime: Long = 0

    constructor(parcel: Parcel) : this() {
        uid = parcel.readString()
        username = parcel.readString()
        phoneNumber = parcel.readString()
        orderAccepted = parcel.readByte() != 0.toByte()
        paymentReceived = parcel.readByte() != 0.toByte()
        itemPushKey = parcel.readString()
        currentTime = parcel.readLong()
    }

    constructor(
        userId: String,
        name: String,
        foodItemNames: ArrayList<String>,
        foodItemPrices: ArrayList<String>,
        foodItemQuantities: ArrayList<Int>,
        phone: String,
        orderAccept: Boolean,
        paymentReceive: Boolean,
        itemPushKeys: String?,
        time: Long
    ) : this(){
        this.uid = userId
        this.username = name
        this.foodNames = foodItemNames
        this.foodPrices = foodItemPrices
        this.foodQuantities = foodItemQuantities
        this.phoneNumber = phone
        this.orderAccepted = orderAccept
        this.paymentReceived = paymentReceive
        this.itemPushKey = itemPushKeys
        this.currentTime = time

    }

    fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(username)
        parcel.writeString(phoneNumber)
        parcel.writeByte(if (orderAccepted) 1 else 0)
        parcel.writeByte(if (paymentReceived) 1 else 0)
        parcel.writeString(itemPushKey)
        parcel.writeLong(currentTime)
    }

    fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderDetails> {
        override fun createFromParcel(parcel: Parcel): OrderDetails {
            return OrderDetails(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetails?> {
            return arrayOfNulls(size)
        }
    }
}

