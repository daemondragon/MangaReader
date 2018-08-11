package com.vikings.mangareader.source

import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.MangasPage
import com.vikings.mangareader.core.Source
import io.reactivex.Observable

/**
 * Source that the SourceManager can return if the wanted [Source]
 * could not be found. Doing so allow to not have to test for null
 * each time a [Source] is wanted.
 */
class NullSource(override val id: Int): Source {

    override val name: String = "Null Source"

    override fun fetchLatestMangas(page: Int): Observable<MangasPage> {
        throw Exception("Source not found. Wanted id: $id")
    }

    override fun fetchMangaInformation(manga: Manga): Observable<Manga> {
        throw Exception("Source not found. Wanted id: $id")
    }
}