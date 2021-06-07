package com.example.bitcointicker.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * Created by Batuhan Duvarci on 12.02.2021.
 */
abstract class BTFragment<BINDING : ViewBinding> : Fragment {
    constructor() : super()

    @ContentView
    constructor(@LayoutRes layourResId : Int) : super(layourResId)

    private var _binding: BINDING? = null
    protected val binding get() = _binding!!

    abstract fun bind(inflater: LayoutInflater, viewGroup: ViewGroup?) : BINDING

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bind(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}