package com.example.bitcointicker.screens.coinlist.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcointicker.R
import com.example.bitcointicker.common.constants.BundleConstants
import com.example.bitcointicker.common.models.CoinModel
import com.example.bitcointicker.databinding.FragmentCoinListBinding
import com.example.bitcointicker.presentation.extensions.hideLoading
import com.example.bitcointicker.presentation.extensions.showLoading
import com.example.bitcointicker.presentation.fragments.BTFragment
import com.example.bitcointicker.screens.coindetail.fragments.CoinDetailFragment
import com.example.bitcointicker.screens.coinlist.adapters.CoinListAdapter
import com.example.bitcointicker.screens.coinlist.viewmodels.CoinListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bt.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoinListFragment : BTFragment<FragmentCoinListBinding>() {

    private lateinit var coinListViewModel: CoinListViewModel
    private val coinListAdapter = CoinListAdapter()
    private lateinit var coinModelList: ArrayList<CoinModel>
    private lateinit var coinDetailFragment: CoinDetailFragment

    override fun bind(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?
    ) = FragmentCoinListBinding.inflate(layoutInflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coinListViewModel = ViewModelProvider(this).get(CoinListViewModel::class.java)
        coinModelList = ArrayList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserInterface()
        getData()
    }

    private fun initUserInterface() {
        binding.toolbar.toolbarBackButton.visibility = View.GONE
        binding.toolbar.toolbarTitleTextView.text =
            resources.getString(R.string.coin_list_fragment_toolbar_title)

        binding.toolbar.rootToolbar.inflateMenu(R.menu.menu_coin_list_toolbar)

        binding.toolbar.rootToolbar.menu.findItem(R.id.search).isVisible = true

        binding.toolbar.toolbarBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.myCoinsButton.visibility = View.VISIBLE

        binding.toolbar.rootToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    binding.toolbar.toolbarTitleTextView.visibility = View.GONE
                    binding.toolbar.toolbarSearchView.visibility = View.VISIBLE
                    binding.toolbar.toolbarSearchView.isSelected = true
                    binding.toolbar.toolbarSearchView.isIconified = false
                    binding.toolbar.toolbarSearchView.requestFocus()
                    menuItem.isVisible = false
                    true
                }
                else -> false
            }
        }

        binding.toolbar.toolbarSearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                coinListAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        binding.toolbar.toolbarSearchView.setOnCloseListener {
            binding.toolbar.toolbarTitleTextView.visibility = View.VISIBLE
            binding.toolbar.toolbarSearchView.visibility = View.GONE
            binding.toolbar.rootToolbar.menu.findItem(R.id.search).isVisible = true
            binding.toolbar.toolbarSearchView.isSelected = false
            coinListAdapter.submitList(coinModelList)
            true
        }

        binding.coinListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.coinListRecyclerView.adapter = coinListAdapter

        coinListViewModel.response.observe(viewLifecycleOwner, { response ->
            coinModelList.addAll(response)
            coinListAdapter.submitList(coinModelList)
            coinListAdapter.notifyDataSetChanged()
        })

        coinListViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            if (isLoading) {
                showLoading(this.childFragmentManager)
            } else {
                hideLoading(this.childFragmentManager)
            }
        })

        coinListAdapter.setOnItemClickListener(object :
            CoinListAdapter.OnItemClickListener {
            override fun onItemClick(item: CoinModel) {
                val bundle = Bundle()
                bundle.putString(BundleConstants.BUNDLE_COIN_ID, item.id)
                coinDetailFragment = CoinDetailFragment()
                coinDetailFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container,
                        coinDetailFragment,
                        CoinDetailFragment.COIN_DETAIL_FRAGMENT
                    )
                    .addToBackStack(null)
                    .commit()
            }
        })

        binding.myCoinsButton.setOnClickListener {
            activateMyCoinsPage()
        }

        coinListViewModel.favoriteList.observe(viewLifecycleOwner, {
            coinListAdapter.submitList(it)
            coinListAdapter.notifyDataSetChanged()
        })
    }

    private fun activateMyCoinsPage(){
        binding.toolbar.toolbarBackButton.visibility = View.VISIBLE
        binding.myCoinsButton.visibility = View.GONE
        binding.toolbar.rootToolbar.menu.findItem(R.id.search).isVisible = false
        getFavorites()
    }

    private fun getData() {
        CoroutineScope(Dispatchers.IO).launch {
            coinListViewModel.getCoins()
        }
    }

    private fun getFavorites() {
        CoroutineScope(Dispatchers.IO).launch {
            coinListViewModel.getFavorites()
        }
    }
}