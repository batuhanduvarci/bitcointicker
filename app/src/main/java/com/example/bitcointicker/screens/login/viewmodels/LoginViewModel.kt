package com.example.bitcointicker.screens.login.viewmodels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitcointicker.common.models.CoinModel
import com.example.bitcointicker.service.ServiceInstance
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * Created by Batuhan Duvarci on 6.06.2021.
 */
@FragmentScoped
class LoginViewModel : ViewModel() {

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> get() = _isLoading

    private var _auth = MutableLiveData<Boolean>()
    val auth : LiveData<Boolean> get() = _auth

    private var _isLoginSuccessful = MutableLiveData<Boolean>()
    val isLoginSuccessful : LiveData<Boolean> get() = _isLoginSuccessful

    fun isUserLoggedIn(){
        viewModelScope.launch {
            _auth.value = FirebaseAuth.getInstance().currentUser != null
        }
    }

    fun login(activity: Activity, email : String, password : String){
        viewModelScope.launch {
            _isLoading.value = true
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) {
                    _isLoginSuccessful.value = it.isSuccessful
                    _isLoading.value = false
                }
        }
    }
}