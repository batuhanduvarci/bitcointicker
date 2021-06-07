package com.example.bitcointicker.common.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Batuhan Duvarci on 5.06.2021.
 */
data class ImageModel(
    @SerializedName("large")
    val large: String,
)
