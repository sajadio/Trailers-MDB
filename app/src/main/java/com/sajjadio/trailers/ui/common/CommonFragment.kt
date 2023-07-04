package com.sajjadio.trailers.ui.common

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.sajjadio.trailers.R
import com.sajjadio.trailers.databinding.FragmentCommonBinding
import com.sajjadio.trailers.ui.base.BaseFragment
import com.sajjadio.trailers.ui.PagingLoadStateAdapter
import com.sajjadio.trailers.utils.Destination
import com.sajjadio.trailers.utils.movieToDestination
import com.sajjadio.trailers.utils.navigateToAnotherDestination
import com.sajjadio.trailers.utils.observeEvent
import com.sajjadio.trailers.utils.onClickBackButton
import com.sajjadio.trailers.utils.setAsActionBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommonFragment :
    BaseFragment<FragmentCommonBinding, CommonViewModel>(R.layout.fragment_common) {

    override val LOG_TAG = this::class.java.simpleName
    override val viewModelClass = CommonViewModel::class.java
    private lateinit var adapter: CommonPagingAdapter
    private val args: CommonFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            onClickBackButton(appBarLayout.toolbar)
            viewModel?.title = args.destination.name

            swipeRefreshLayout.setOnRefreshListener {
                adapter?.refresh()
                swipeRefreshLayout.isRefreshing = false
            }
            checkDestinationID(args.destination)
        }

        viewModel.clickItemEvent.observeEvent(viewLifecycleOwner){
            navigateToAnotherDestination(
                CommonFragmentDirections.actionCommonFragmentToMovieFragment(it)
            )
        }
    }

    private fun checkDestinationID(destinationType: Destination) {
        viewModel.checkDestination(destinationType)
        initialAdapter()
    }


    private fun initialAdapter() {
        adapter = CommonPagingAdapter(viewModel)

        binding.rcCommon.layoutManager = GridLayoutManager(context, 2)
        viewModel.responseCommonPagingData.observe(viewLifecycleOwner) { data ->
            lifecycleScope.launch {
                adapter.submitData(data)
            }
        }

        binding.rcCommon.hasFixedSize()
        loadStateAdapter()
    }

    private fun loadStateAdapter() {
        binding.rcCommon.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter { adapter.retry() },
            footer = PagingLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadState ->
            val isEmptyList = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            showEmptyList(!isEmptyList)

            binding.rcCommon.isVisible = loadState.source.refresh is LoadState.NotLoading
            (loadState.source.refresh is LoadState.Loading).also {
                stateManagement(it)
            }
        }
    }

    private fun showEmptyList(emptyList: Boolean) {
        binding.rcCommon.isVisible = !emptyList
    }

    private fun stateManagement(state: Boolean) {
        if (state)
            binding.shimmer.startShimmer()
        else
            binding.shimmer.stopShimmer()

        binding.shimmer.isVisible = state
        binding.swipeRefreshLayout.isVisible = !state

    }
}