package com.example.bitcointicker.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bitcointicker.R
import com.example.bitcointicker.databinding.ActivityBtBinding
import com.example.bitcointicker.screens.login.fragments.LoginFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BTActivity : AppCompatActivity() {

    private lateinit var loginFragment: LoginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityBtBinding = ActivityBtBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginFragment = LoginFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.container, loginFragment, "LoginFragment")
            .addToBackStack(null)
            .commit()

    }
}