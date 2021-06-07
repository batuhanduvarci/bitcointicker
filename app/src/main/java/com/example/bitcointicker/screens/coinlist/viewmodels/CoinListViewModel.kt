package com.example.bitcointicker.screens.coinlist.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitcointicker.common.constants.ServiceConstants
import com.example.bitcointicker.common.models.CoinModel
import com.example.bitcointicker.screens.coindetail.viewmodels.CoinDetailViewModel
import com.example.bitcointicker.service.ServiceInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * Created by batuhan.duvarci on 2/13/21.
 */
@FragmentScoped
class CoinListViewModel : ViewModel() {

    private var _response = MutableLiveData<List<CoinModel>>()
    val response: LiveData<List<CoinModel>> get() = _response

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _favoriteList = MutableLiveData<List<CoinModel>>()
    val favoriteList: LiveData<List<CoinModel>> get() = _favoriteList

    fun getCoins() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _response.value = ServiceInstance.serviceApiInstance.getCoins()
                _isLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            var coinList = arrayListOf<CoinModel>()
            FirebaseAuth.getInstance().currentUser?.let {
                val db = Firebase.firestore
                db.collection(ServiceConstants.COLLECTION_NAME)
                    .document(it.uid)
                    .collection(ServiceConstants.COLLECTION_NAME)
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            coinList.add(
                                CoinModel(
                                    document.data[ServiceConstants.ID_FIELD] as String,
                                    document.data[ServiceConstants.SYMBOL_FIELD] as String,
                                    document.data[ServiceConstants.NAME_FIELD] as String
                                )
                            )
                        }
                        _favoriteList.value = coinList
                        _isLoading.value = false
                    }
                    .addOnFailureListener {
                        _isLoading.value = false
                    }
            }
        }
    }
}