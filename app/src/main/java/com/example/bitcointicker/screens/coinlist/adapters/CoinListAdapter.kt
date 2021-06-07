package com.example.bitcointicker.screens.coinlist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.bitcointicker.R
import com.example.bitcointicker.common.models.CoinModel
import com.example.bitcointicker.screens.coinlist.viewholders.CoinViewHolder
import kotlinx.android.synthetic.main.item_coin_list.view.*

class CoinListAdapter : ListAdapter<CoinModel, CoinViewHolder>(CoinListItemDiffCallback()),
    Filterable {

    private var onItemClickListener: OnItemClickListener? = null
    private lateinit var unfilteredList: List<CoinModel>
    private lateinit var coinModelFilteredList: List<CoinModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_coin_list, parent, false)

        return CoinViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)

        holder.itemView.itemContainer.setOnClickListener {
            if (onItemClickListener == null) {
                return@setOnClickListener
            }
            onItemClickListener?.onItemClick(item)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                coinModelFilteredList = if (charString.isEmpty()) {
                    currentList
                } else {
                    val filteredList: ArrayList<CoinModel> = arrayListOf()
                    for (coin in currentList) {
                        if (coin.name.contains(charString, true) || coin.symbol.contains(
                                charString,
                                true
                            )
                        ) {
                            filteredList.add(coin)
                        }
                    }
                    filteredList
                }

                val filterResults: FilterResults = object : FilterResults() {}
                filterResults.values = coinModelFilteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                coinModelFilteredList = (results?.values as List<CoinModel>)
                submitList(coinModelFilteredList)
            }

        }

    }

    override fun onCurrentListChanged(
        previousList: MutableList<CoinModel>,
        currentList: MutableList<CoinModel>
    ) {
        this.unfilteredList = previousList
    }

    class CoinListItemDiffCallback : DiffUtil.ItemCallback<CoinModel>() {
        override fun areItemsTheSame(
            oldItem: CoinModel,
            newItem: CoinModel
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: CoinModel,
            newItem: CoinModel
        ): Boolean = oldItem == newItem
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(item: CoinModel)
    }
}