package com.example.bitcointicker.common.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Batuhan Duvarci on 5.06.2021.
 */
data class MarketDataModel(
    @SerializedName("current_price")
    val currentPrice: PriceModel,
    @SerializedName("ath_change_percentage")
    val changePercentage: ChangePercentageModel
)
