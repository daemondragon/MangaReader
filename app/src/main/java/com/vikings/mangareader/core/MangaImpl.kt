package com.vikings.mangareader.core

/**
 * See [Manga] for fields explanation.
 */
class MangaImpl: Manga {
    override lateinit var name: String

    override var sourceId: Int = -1

    override lateinit var url: String

    override var coverUrl: String? = null

    override var summary: String? = null

    override var authors: List<String>? = null

    override var genres: List<String>? = null

    override var rating: Float? = null

    override var status: Manga.Status = Manga.Status.Unknown

    override var favorite: Boolean? = null

    override var automaticDownload: Boolean? = null
}