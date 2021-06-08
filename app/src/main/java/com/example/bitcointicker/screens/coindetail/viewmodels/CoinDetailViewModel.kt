package com.example.bitcointicker.screens.coindetail.viewmodels

import android.app.Service
import android.content.Context
import androidx.lifecycle.*
import androidx.work.*
import com.example.bitcointicker.common.models.CoinDetailModel
import com.example.bitcointicker.service.ServiceInstance
import com.example.bitcointicker.service.ServiceWorker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * Created by Batuhan Duvarci on 5.06.2021.
 */
@FragmentScoped
class CoinDetailViewModel : ViewModel() {

    private var _response = MutableLiveData<CoinDetailModel>()
    val response: LiveData<CoinDetailModel> get() = _response

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _isAdded = MutableLiveData<Boolean>()
    val isAdded: LiveData<Boolean> get() = _isAdded

    private var _price = MutableLiveData<String>()
    val price: LiveData<String> get() = _price

    fun getCoin(id: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _response.value = ServiceInstance.serviceApiInstance.getCoin(id)
                _isLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getFavorites(id: String?, symbol: String?, name: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            FirebaseAuth.getInstance().currentUser?.let {
                val db = Firebase.firestore
                var isAddable = true
                db.collection(COLLECTION_NAME)
                    .document(it.uid)
                    .collection(COLLECTION_NAME)
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            if (document.data[ID_FIELD] == id) {
                                isAddable = false
                            }
                        }
                        if (isAddable) {
                            addToFavorites(id, symbol, name)
                        } else {
                            _isAdded.value = false
                            _isLoading.value = false
                        }
                    }
                    .addOnFailureListener {
                        _isAdded.value = false
                        _isLoading.value = false
                    }
            }

        }
    }

    fun addToFavorites(id: String?, symbol: String?, name: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            id?.let {
                val db = Firebase.firestore
                val data = hashMapOf(
                    "id" to id,
                    "symbol" to symbol,
                    "name" to name
                )
                FirebaseAuth.getInstance().currentUser?.let {
                    db.collection(COLLECTION_NAME)
                        .document(it.uid)
                        .collection(COLLECTION_NAME)
                        .add(data)
                        .addOnSuccessListener {
                            _isAdded.value = true
                            _isLoading.value = false
                        }
                        .addOnFailureListener {
                            _isAdded.value = false
                            _isLoading.value = false
                        }
                }
            } ?: kotlin.run {
                _isAdded.value = false
            }
        }
    }

    fun initWorker(context: Context, viewLifeCycleOwner: LifecycleOwner, id: String) {
        val workerRequest =
            PeriodicWorkRequest.Builder(ServiceWorker::class.java, 15, TimeUnit.MINUTES)
                .setInputData(Data.Builder().putString(ID_FIELD, id).build()).build()
        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniquePeriodicWork(
            COIN_PRICE_WORKER,
            ExistingPeriodicWorkPolicy.KEEP,
            workerRequest
        )

        workManager.getWorkInfoByIdLiveData(workerRequest.id).observe(viewLifeCycleOwner,
            Observer {
                if (it.state.isFinished) {
                    _price.value = it.outputData.getString(ServiceWorker.DATA_FROM_WORKER)
                }
            })
    }

    fun disableWorker(context: Context) {
        WorkManager.getInstance(context).cancelAllWork()
    }

    companion object {
        const val COLLECTION_NAME = "favorites"
        const val ID_FIELD = "id"
        const val COIN_PRICE_WORKER = "COIN_PRICE_WORKER"
    }
}