package com.vikings.mangareader.core

import android.graphics.drawable.Drawable
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
     *
     * Any negative id (< 0) is reserved for error purpose.
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

    /**
     * Load the cover of the manga. It's not included in
     * manga information as fetching this information usually
     * require and extra indirection.
     */
    fun fetchMangaCover(manga: Manga): Observable<Drawable>

    /**
     * Load all chapter information.
     * Note that subsequent call to [fetchMangaInformation] and [fetchChapterInformation]
     * must get all chapter information if both call succeeded.
     * It's not expected to have this call retrieve all information (except [Page])
     */
    fun fetchChapterInformation(chapter: Chapter): Observable<Chapter>

    /**
     * Load the picture associated with the page.
     */
    fun fetchPagePicture(page: Page): Observable<Drawable>
}