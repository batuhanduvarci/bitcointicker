package com.example.bitcointicker.common.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Batuhan Duvarci on 5.06.2021.
 */
data class PriceModel(
    @SerializedName("usd")
    val usd: Double,
    @SerializedName("eur")
    val eur: Double,
)
