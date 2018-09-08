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
     * Get all categories that the source support.
     * For an online source, the categories could be: Popular manga, latest manga...
     * The first element of the pair is the name to display (can be internationalized)
     * while the second is constant and used to identify the category.
     *
     * Must have at least one category.
     */
    fun getCategories(): List<Pair<String, String>>

    /**
     * Get a mangas list for the given category at the give page (0 indexed).
     * categoryKey will always be present in the [getCategories] result.
     */
    fun fetchMangasBy(categoryKey: String, page: Int): Observable<MangasPage>

    /**
     * Search the manga name at the given page (0 indexed).
     */
    fun fetchSearch(mangaName: String, page: Int): Observable<MangasPage>

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