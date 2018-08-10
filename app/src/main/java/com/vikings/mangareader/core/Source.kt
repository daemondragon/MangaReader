package com.vikings.mangareader.core

/**
 * Anything that can be used as source for manga.
 * Source can either be websites, local storage...
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
}