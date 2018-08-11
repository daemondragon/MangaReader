package com.vikings.mangareader.core

import io.reactivex.Observable

/**
 * Anything that can be used as source for manga.
 * Source can either be websites, local storage...
 *
 * All fetch operations are expected to be run in a separate
 * thread if needed to not stop the UI thread.
 *
 * All pages index start at 0. That means that if a website
 * have its page index starting at one, the related source
 * will have to adjust the number to match with the starting index.
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
     * Get the latest manga.
     * TODO: instead of having fetchXXXMangas(page), have fetchManga(type, page) where type is source dependent.
     */
    fun fetchLatestMangas(page: Int): Observable<MangasPage>

    /**
     * Load information about the manga.
     * Add the end, it is not expected that all information
     * have been filled if an error occurred for instance.
     */
    fun fetchMangaInformation(manga: Manga): Observable<Manga>
}