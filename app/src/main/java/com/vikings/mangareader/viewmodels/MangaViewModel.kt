package com.vikings.mangareader.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.drawable.Drawable
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.SourceManager


class MangaViewModel(private val manga: Manga): ViewModel() {
    private val mangaLiveData = MutableLiveData<Manga>()
    private val mangaCoverLiveData = MutableLiveData<Drawable>()

    private val errorsLiveData = MutableLiveData<String>()
    private val loadingLiveData = MutableLiveData<Boolean>()

    private var onError = false//If set to true, no more request is allowed.

    fun getManga(): LiveData<Manga> {
        return mangaLiveData
    }

    fun getMangaCover(): LiveData<Drawable> {
        return mangaCoverLiveData
    }

    /**
     * Get all errors when they come
     */
    fun getErrors(): LiveData<String> {
        return errorsLiveData
    }

    /**
     * Used to know when the loading state.
     */
    fun getLoadingState(): LiveData<Boolean> {
        return loadingLiveData
    }

    fun fetchMangaInformation() {
        synchronized(this) {
            if (loadingLiveData.value != true && !onError) {
                loadingLiveData.value = true

                val source = SourceManager.get(manga.sourceId)

                source.fetchMangaInformation(manga)
                    .subscribe(
                        { manga ->
                            synchronized(this) {
                                mangaLiveData.value = manga

                                loadingLiveData.value = false

                                source.fetchMangaCover(manga)
                                    .subscribe(
                                        { picture -> mangaCoverLiveData.value = picture },
                                        { _ -> /* Do nothing in case of error */ })

                            }
                        },
                        { error ->
                            synchronized(this) {
                                onError = true
                                errorsLiveData.value = error.toString()
                                loadingLiveData.value = false
                            }
                        })
            }
        }
    }

    fun errorHandled() {
        synchronized(this) {
            onError = false
            errorsLiveData.value = null//Prevent having a reload on rotation
        }
    }

    override fun onCleared() {
        super.onCleared()

        manga.dispose()//To clear chapter information
    }
}