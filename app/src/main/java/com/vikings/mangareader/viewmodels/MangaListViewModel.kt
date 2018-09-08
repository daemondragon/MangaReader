package com.vikings.mangareader.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.Source
import com.vikings.mangareader.core.SourceManager

/**
 * TODO:
 * - add allows retry function: when an error occur, [requestMoreMangas] does nothing,
 *      even if there is more manga to load. calling this function allows the retry.
 * - add search and category support.
 */
class MangaListViewModel(sourceId: Int): ViewModel() {
    private val source = SourceManager.get(sourceId)

    private val mangaList = mutableListOf<Manga>()

    private val mangaListLiveData = MutableLiveData<List<Manga>>()
    private val errors = MutableLiveData<String>()

    private var nextPage = 0//Next page to load.
    private var hasNextPage = true//Is there a page to load next
    private var loading = false//To not load a page twice

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
        return errors
    }

    /**
     * Tell the source to load more mangas.
     * @return true if there is more mangas to load, false otherwise
     */
    fun requestMoreMangas(): Boolean {
        synchronized(this) {
            if (!hasNextPage)
                return false

            if (!loading) {
                loading = true//Something to load, load it

                source.fetchMangasBy(source.getCategories()[0].second, nextPage)
                    .subscribe({ mangasPage ->
                        synchronized(this) {
                            hasNextPage = mangasPage.hasNext
                            ++nextPage

                            mangaList.addAll(mangasPage.mangas)
                            mangaListLiveData.value = mangaList

                            loading = false
                        }

                    }, { error ->
                        synchronized(this) {
                            errors.value = error.toString()
                            //To prevent being notified again when the user rotate the screen
                            errors.value = null

                            loading = false
                        }
                    })
            }
            return hasNextPage
        }
    }
}