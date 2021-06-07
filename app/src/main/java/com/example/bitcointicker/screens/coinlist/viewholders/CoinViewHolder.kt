package com.example.bitcointicker.screens.coinlist.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcointicker.common.models.CoinModel
import com.example.bitcointicker.databinding.ItemCoinListBinding

class CoinViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    val binding = ItemCoinListBinding.bind(view)

    fun bind(item : CoinModel){
        binding.symbolTextView.text = item.symbol
        binding.nameTextView.text = item.name
    }

}