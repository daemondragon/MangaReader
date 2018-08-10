package com.vikings.mangareader.source

import com.vikings.mangareader.core.Source

class Mangakakalot: Source {
    /**
     * Unique identifier of the source.
     * Is used to know which source have created the manga.
     */
    override val id: Int = 1
    /**
     * The name of the source.
     * Must be internationalized.
     */
    override val name: String = "Mangakakalot"
}