package com.example.bitcointicker.service

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.bitcointicker.common.models.CoinDetailModel
import com.example.bitcointicker.screens.coindetail.viewmodels.CoinDetailViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Batuhan Duvarci on 8.06.2021.
 */
class ServiceWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    override fun doWork(): Result {
        val coinId = inputData.getString(CoinDetailViewModel.ID_FIELD)
        var updatedPrice: String? = null
        coinId?.let {
            val call = ServiceInstance.serviceApiInstance.getCoinWorker(it)
            call.enqueue(object : Callback<CoinDetailModel> {
                override fun onResponse(
                    call: Call<CoinDetailModel>,
                    response: Response<CoinDetailModel>
                ) {
                    response.body()?.let {
                        updatedPrice = it.marketData.currentPrice.usd.toString()
                    }
                }

                override fun onFailure(call: Call<CoinDetailModel>, t: Throwable) {

                }

            })
        }

        return if (updatedPrice != null) {
            Result.success(Data.Builder().putString(DATA_FROM_WORKER, updatedPrice).build())
        } else {
            Result.failure()
        }
    }

    companion object {
        const val DATA_FROM_WORKER = "DATA_FROM_WORKER"
    }
}