package com.sajjadio.trailers.ui.search


import com.sajjadio.trailers.R
import com.sajjadio.trailers.domain.model.CommonResult
import com.sajjadio.trailers.ui.base.BaseInteractListener
import com.sajjadio.trailers.ui.base.BasePagingAdapter

class SearchPagingAdapter(
    listener: BaseInteractListener
) : BasePagingAdapter<CommonResult>(listener) {
    override var layoutId = R.layout.layout_search_card
}