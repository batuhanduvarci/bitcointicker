package com.example.bitcointicker.presentation.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.bitcointicker.R

/**
 * Created by batuhan.duvarci on 2/13/21.
 */
class ProgressDialogFragment : DialogFragment() {

    fun newInstance() : ProgressDialogFragment{
        val args = Bundle()
        
        val fragment = ProgressDialogFragment()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return inflater.inflate(R.layout.fragment_progress_dialog, container, false)
    }

}