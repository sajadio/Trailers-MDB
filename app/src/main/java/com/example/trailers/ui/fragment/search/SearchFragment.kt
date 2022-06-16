package com.example.trailers.ui.fragment.search

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.trailers.R
import com.example.trailers.databinding.FragmentSearchBinding
import com.example.trailers.ui.base.BaseFragment
import com.example.trailers.ui.fragment.search.adapter.PagingLoadStateAdapter
import com.example.trailers.ui.fragment.search.adapter.SearchPagingAdapter
import com.example.trailers.ui.fragment.search.vm.SearchViewModel
import com.example.trailers.utils.movieToDestination

import com.example.trailers.utils.setAsActionBar
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search),
    androidx.appcompat.widget.SearchView.OnQueryTextListener {

    @Inject
    lateinit var vm: SearchViewModel
    lateinit var adapter: SearchPagingAdapter

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.setAsActionBar(binding.toolbar,
            true,
            resources.getString(R.string.search))

        binding.rcSearch.setOnTouchListener { _, _ ->
            hidePartialKeyboard()
        }
        initialAdapter()

        binding.swiperefreshlayout.setOnRefreshListener {
            adapter.refresh()
            binding.swiperefreshlayout.isRefreshing = false
        }
    }

    private fun initialAdapter() {

        adapter = SearchPagingAdapter()
        adapter.onItemClickListener {
            it?.let { id ->
                val action = SearchFragmentDirections.actionSearchFragmentToMoiveFragment(id)
                action.movieToDestination(view)
            }
        }

        binding.rcSearch.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter { adapter.retry() },
            footer = PagingLoadStateAdapter { adapter.retry() }
        )

        vm.getMoviesSearch.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                it?.let {
                    adapter.submitData(it)
                    binding.rcSearch.hasFixedSize()
                }
            }
        }

        adapter.addLoadStateListener { loadState ->
            val isEmptyList = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            showEmptyList(isEmptyList)

            // Only show the list if refresh succeeds
            binding.rcSearch.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh
            (loadState.source.refresh is LoadState.Loading).also {
                binding.progressBar.isVisible = it
            }

        }
    }

    private fun hidePartialKeyboard(): Boolean {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun showEmptyList(emptyList: Boolean) {
        binding.rcSearch.isVisible = !emptyList
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar, menu)

        val search = menu.findItem(R.id.search)
        val searchView = search?.actionView as? androidx.appcompat.widget.SearchView
        searchView?.queryHint = resources.getString(R.string.search_box)
        searchView?.setOnQueryTextListener(this)
        searchView?.maxWidth = Integer.MAX_VALUE

        super.onCreateOptionsMenu(menu, inflater)

    }


    override fun onQueryTextSubmit(query: String?) = false

    override fun onQueryTextChange(query: String?): Boolean {
        lifecycleScope.launch(Dispatchers.Main) {
            query?.let {
                if (it.length >= 3) {
                    delay(1000L)
                    vm.getSearch(it)
                }
            }
        }
        return true
    }

}