package com.vikings.mangareader.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class MangaListViewModelFactory(private val sourceId: Int): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MangaListViewModel::class.java))
            return MangaListViewModel(sourceId) as T
        else
            throw IllegalArgumentException("expected MangaListViewModel as argument")
    }

}