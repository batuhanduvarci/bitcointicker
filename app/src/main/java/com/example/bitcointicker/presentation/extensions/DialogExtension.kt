package com.example.bitcointicker.presentation.extensions

import androidx.fragment.app.FragmentManager
import com.example.bitcointicker.presentation.dialogs.ProgressDialogFragment

/**
 * Created by batuhan.duvarci on 2/13/21.
 */
fun showLoading(fragmentManager : FragmentManager) {
    val progressDialogFragment = ProgressDialogFragment().newInstance()

    fragmentManager.beginTransaction()
        .addToBackStack(null)

    progressDialogFragment.show(fragmentManager, "progressDialog")
}

fun hideLoading(fragmentManager: FragmentManager){
    val progressDialogFragment = fragmentManager.findFragmentByTag("progressDialog") as ProgressDialogFragment?
    progressDialogFragment?.dismiss()
}