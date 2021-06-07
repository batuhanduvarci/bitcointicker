package com.example.bitcointicker.screens.coindetail.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bitcointicker.R
import com.example.bitcointicker.common.constants.BundleConstants
import com.example.bitcointicker.common.models.CoinDetailModel
import com.example.bitcointicker.databinding.FragmentCharacterDetailBinding
import com.example.bitcointicker.presentation.extensions.hideLoading
import com.example.bitcointicker.presentation.extensions.showLoading
import com.example.bitcointicker.presentation.fragments.BTFragment
import com.example.bitcointicker.screens.coindetail.viewmodels.CoinDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bt.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs

@AndroidEntryPoint
class CoinDetailFragment : BTFragment<FragmentCharacterDetailBinding>() {

    private lateinit var coinDetailViewModel: CoinDetailViewModel
    private var coinId: String? = null
    private val backgroundExecuter = Executors.newSingleThreadScheduledExecutor()
    private var currentPriceBeforeUpdateInterval = 0.0
    private lateinit var coinModel : CoinDetailModel

    override fun bind(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?
    ) = FragmentCharacterDetailBinding.inflate(layoutInflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coinDetailViewModel = ViewModelProvider(this).get(CoinDetailViewModel::class.java)
        arguments?.let {
            coinId = it.getString(BundleConstants.BUNDLE_COIN_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserInterface()
        coinId?.let {
            getCoin(it)
        }
    }

    private fun initUserInterface() {
        binding.toolbar.toolbarBackButton.visibility = View.VISIBLE

        binding.toolbar.toolbarBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        coinDetailViewModel.response.observe(viewLifecycleOwner, { response ->
            populateDetails(response)
            if (currentPriceBeforeUpdateInterval == 0.0 && !backgroundExecuter.isShutdown){
                currentPriceBeforeUpdateInterval = response.marketData.currentPrice.usd
            }
        })

        coinDetailViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            if (isLoading) {
                showLoading(this.childFragmentManager)
            } else {
                hideLoading(this.childFragmentManager)
            }
        })

        binding.updateIntervalButton.setOnClickListener {
            activateExecuter(binding.updateIntervalEditText.text.toString().toLong())
        }

        binding.addToFavoritesButton.setOnClickListener{
            addToFavorites()
        }

        coinDetailViewModel.isAdded.observe(viewLifecycleOwner, {
            if (it){
                Toast.makeText(
                    requireContext(),
                    "Successfuly Added to My Coins",
                    Toast.LENGTH_LONG
                ).show()
            }else{
                Toast.makeText(
                    requireContext(),
                    "Error While Adding to My Coins",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun getCoin(id: String){
        CoroutineScope(Dispatchers.IO).launch {
            coinDetailViewModel.getCoin(id)
        }
    }

    private fun addToFavorites(){
        CoroutineScope(Dispatchers.IO).launch {
            coinDetailViewModel.getFavorites(coinModel.id, coinModel.symbol, coinModel.name)
        }
    }

    private fun populateDetails(model: CoinDetailModel){
        coinModel = model
        binding.toolbar.toolbarTitleTextView.text = model.name
        Glide.with(this).load(model.image.large).into(binding.coinImageView)
        binding.hashingAlgorithmTextView.text = getString(R.string.coin_detail_fragment_hashing_algorithm_text, model.hashingAlgorithm)
        if (model.description.en.isEmpty()){
            binding.descriptionScrollView.visibility = View.GONE
        }else{
            binding.descriptionScrollView.visibility = View.VISIBLE
        }
        binding.descriptionTextView.movementMethod = ScrollingMovementMethod()
        binding.descriptionTextView.text = getString(R.string.coin_detail_fragment_description_text, model.description.en)
        binding.currentPriceTextView.text = getString(R.string.coin_detail_fragment_current_price_text, model.marketData.currentPrice.usd.toString(), currentPriceBeforeUpdateInterval.toString())
        binding.currentPriceTextView.isSelected = true
        if (model.marketData.changePercentage.usd < 0){
            binding.priceChangeTextView.setTextColor(resources.getColor(R.color.red, requireActivity().theme))
        }else if (model.marketData.changePercentage.usd >= 0) {
            binding.priceChangeTextView.setTextColor(resources.getColor(R.color.green, requireActivity().theme))
        }
        binding.priceChangeTextView.text = getString(R.string.coin_detail_fragment_price_change_text, abs(model.marketData.changePercentage.usd).toString())
    }

    private fun activateExecuter(seconds : Long){
        backgroundExecuter.scheduleWithFixedDelay({
            coinId?.let {
                getCoin(it)
            }
        }, 0, seconds, TimeUnit.SECONDS)
    }

    companion object{
        const val COIN_DETAIL_FRAGMENT = "COIN_DETAIL_FRAGMENT"
    }

    override fun onDestroyView() {
        backgroundExecuter.shutdown()
        super.onDestroyView()
    }
}