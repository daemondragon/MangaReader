package com.vikings.mangareader.core

import io.reactivex.Observable

/**
 * Anything that can be used as source for manga.
 * Source can either be websites, local storage...
 *
 * All fetch operations are expected to be run in a separate
 * thread if needed to not stop the UI thread.
 */
interface Source {
    /**
     * Unique identifier of the source.
     * Is used to know which source have created the manga.
     */
    val id: Int
    /**
     * The name of the source.
     * Must be internationalized.
     */
    val name: String

    /**
     * Load information about the manga.
     * Add the end, it is not expected that all information
     * have been filled if an error occurred for instance.
     */
    fun fetchMangaInformation(manga: Manga): Observable<Manga>
}