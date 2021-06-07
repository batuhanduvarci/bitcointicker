package com.example.bitcointicker.screens.coindetail.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitcointicker.common.models.CoinDetailModel
import com.example.bitcointicker.service.ServiceInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * Created by Batuhan Duvarci on 5.06.2021.
 */
@FragmentScoped
class CoinDetailViewModel : ViewModel() {

    private var _response = MutableLiveData<CoinDetailModel>()
    val response : LiveData<CoinDetailModel> get() = _response

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> get() = _isLoading

    private var _isAdded = MutableLiveData<Boolean>()
    val isAdded : LiveData<Boolean> get() = _isAdded

    fun getCoin(id : String){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _response.value = ServiceInstance.serviceApiInstance.getCoin(id)
                _isLoading.value = false
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    fun getFavorites(id: String?, symbol: String?, name: String?){
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
                        for (document in result){
                            if (document.data[ID_FIELD] == id){
                                isAddable = false
                            }
                        }
                        if (isAddable){
                            addToFavorites(id, symbol, name)
                        }else{
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

    fun addToFavorites(id: String?, symbol: String?, name: String?){
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

    companion object{
        const val COLLECTION_NAME = "favorites"
        const val ID_FIELD = "id"
    }
}