package com.example.bitcointicker.common.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CoinModel(
    @SerializedName("id")
    val id: String,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("name")
    val name: String
) : Serializable