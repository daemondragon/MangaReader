package com.vikings.mangareader.core

/**
 * Contains a page of mangas.
 * Allow to parse correctly manga with multiple page for search query,
 * and to limit memory utilisation.
 */
data class MangasPage(
    /**
     * All manga in the current page.
     * They are not expected to be fully loaded at this point.
     */
    val mangas: List<Manga>,
    /**
     * Does this page has a next one.
     * If not, there is not need to try to load it.
     */
    val hasNext: Boolean
)