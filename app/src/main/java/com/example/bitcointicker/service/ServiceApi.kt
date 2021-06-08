package com.example.bitcointicker.service

import com.example.bitcointicker.common.models.CoinDetailModel
import com.example.bitcointicker.common.models.CoinModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by batuhan.duvarci on 2/13/21.
 */
interface ServiceApi {

    @GET("coins/list")
    suspend fun getCoins(): List<CoinModel>

    @GET("coins/{id}")
    suspend fun getCoin(@Path("id") id: String): CoinDetailModel

    @GET("coins/{id}")
     fun getCoinWorker(@Path("id") id: String): Call<CoinDetailModel>
}