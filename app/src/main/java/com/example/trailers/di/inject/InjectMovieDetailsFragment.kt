package com.example.trailers.di.inject

import com.example.trailers.di.annotate.FragmentScope
import com.example.trailers.di.module.ProviderViewModel
import com.example.trailers.di.module.ViewModelProvide
import com.example.trailers.ui.fragment.movie.MovieFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module(includes = [ViewModelProvide::class])
abstract class InjectMovieDetailsFragment {

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProviderViewModel::class])
    abstract fun bind(): MovieFragment
}