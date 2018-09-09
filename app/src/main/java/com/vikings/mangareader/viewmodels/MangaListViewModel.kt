package com.vikings.mangareader.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.Source
import com.vikings.mangareader.core.SourceManager

/**
 * TODO: add search and category support.
 */
class MangaListViewModel(sourceId: Int): ViewModel() {
    private val source = SourceManager.get(sourceId)

    private val mangaList = mutableListOf<Manga>()

    private val mangaListLiveData = MutableLiveData<List<Manga>>()
    private val errorsLiveData = MutableLiveData<String>()
    private val loadingLiveData = MutableLiveData<Boolean>()

    private var nextPage = 0//Next page to load.
    private var hasNextPage = true//Is there a page to load next

    private var onError = false//If set to true, no more request is allowed.

    init {
        requestMoreMangas()
    }

    fun getSource(): Source {
        return source
    }

    /**
     * Get an up-to-data list of mangas.
     */
    fun getMangaList(): LiveData<List<Manga>> {
        return mangaListLiveData
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

    /**
     * Tell the source to load more mangas.
     * @return true if there is more mangas to load, false otherwise
     */
    fun requestMoreMangas(): Boolean {
        synchronized(this) {
            if (!hasNextPage)
                return false

            if (loadingLiveData.value != true && !onError) {
                loadingLiveData.value = true

                source.fetchMangasBy(source.getCategories()[0].second, nextPage)
                    .subscribe(
                        { mangasPage ->
                            synchronized(this) {
                                hasNextPage = mangasPage.hasNext
                                ++nextPage

                                mangaList.addAll(mangasPage.mangas)
                                mangaListLiveData.value = mangaList

                                loadingLiveData.value = false
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
            return hasNextPage
        }
    }

    fun errorHandled() {
        synchronized(this) {
            onError = false
            errorsLiveData.value = null//Prevent having a reload on rotation
        }
    }
}