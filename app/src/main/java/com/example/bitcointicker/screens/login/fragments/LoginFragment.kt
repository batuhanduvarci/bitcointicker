package com.example.bitcointicker.screens.login.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.bitcointicker.R
import com.example.bitcointicker.databinding.FragmentLoginBinding
import com.example.bitcointicker.presentation.extensions.hideLoading
import com.example.bitcointicker.presentation.extensions.showLoading
import com.example.bitcointicker.presentation.fragments.BTFragment
import com.example.bitcointicker.screens.coinlist.fragments.CoinListFragment
import com.example.bitcointicker.screens.login.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bt.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Batuhan Duvarci on 6.06.2021.
 */
@AndroidEntryPoint
class LoginFragment : BTFragment<FragmentLoginBinding>() {

    private lateinit var loginViewModel : LoginViewModel
    private lateinit var coinListFragment: CoinListFragment

    override fun bind(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?
    ) = FragmentLoginBinding.inflate(layoutInflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        isUserLoggedIn()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserInterface()
    }

    private fun initUserInterface(){
        binding.loginButton.setOnClickListener {
            login(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
        }

        binding.continueAsGuestButton.setOnClickListener {
            navigateToCoinListFragment()
        }

        loginViewModel.isLoading.observe(viewLifecycleOwner, {
            if (it){
                showLoading(this.childFragmentManager)
            }else{
                hideLoading(this.childFragmentManager)
            }
        })

        loginViewModel.auth.observe(viewLifecycleOwner, {
            if (it){
                navigateToCoinListFragment()
            }
        })

        loginViewModel.isLoginSuccessful.observe(viewLifecycleOwner, {
            if (it){
                navigateToCoinListFragment()
            }else{
                Toast.makeText(
                    requireContext(),
                    "Cannot Login",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun navigateToCoinListFragment(){
        coinListFragment = CoinListFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.container, coinListFragment, "CoinListFragment")
            .addToBackStack(null)
            .commit()
    }

    private fun isUserLoggedIn(){
        CoroutineScope(Dispatchers.IO).launch {
            loginViewModel.isUserLoggedIn()
        }
    }

    private fun login(email: String, password: String){
        CoroutineScope(Dispatchers.IO).launch {
            loginViewModel.login(requireActivity(), email, password)
        }
    }
}