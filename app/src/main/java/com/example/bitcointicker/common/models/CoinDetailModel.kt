package com.example.bitcointicker.common.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Duvarci on 5.06.2021.
 */
data class CoinDetailModel (
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("hashing_algorithm")
    val hashingAlgorithm: String,
    @SerializedName("description")
    val description: DescriptionModel,
    @SerializedName("image")
    val image: ImageModel,
    @SerializedName("market_data")
    val marketData: MarketDataModel
) : Serializable