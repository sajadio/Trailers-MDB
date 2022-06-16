package com.example.trailers.data.repository.common

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.trailers.data.model.movie.common.CommonResult
import com.example.trailers.data.network.ApiService
import com.example.trailers.utils.Constant

class PopularPagingSource(
    private val api: ApiService,
) : PagingSource<Int, CommonResult>() {

    override fun getRefreshKey(state: PagingState<Int, CommonResult>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommonResult> {
        val pageNumber = params.key ?: Constant.DEFAULT_PAGE_INDEX
        return try {
            val response = api.getPopularMovie(page = pageNumber)
            val data = if (response.isSuccessful) response.body()?.results else null
            LoadResult.Page(
                data = data ?: emptyList(),
                prevKey = if (pageNumber == Constant.DEFAULT_PAGE_INDEX) null else pageNumber.minus(1),
                nextKey = if (data?.isEmpty() == true) null else pageNumber.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}