package com.vikings.mangareader.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.vikings.mangareader.core.Manga

class MangaViewModelFactory(private val manga: Manga): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MangaViewModel::class.java))
            return MangaViewModel(manga) as T
        else
            throw IllegalArgumentException("expected MangaViewModel as argument")
    }

}