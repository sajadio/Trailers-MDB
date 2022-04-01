package com.example.movie.data.model

import androidx.annotation.DrawableRes

data class Category(
    val ids: Int,
    val title: String,
    val type: Type,
    val rate: Float,
    @field:DrawableRes
    val posterId: Int,
    val viewMore: Boolean
)