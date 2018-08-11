package com.vikings.mangareader.source

import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.Source
import io.reactivex.Observable

/**
 * Source that the [SourceManager] can return if the wanted [Source]
 * could not be found. Doing so allow to not have to test for null
 * each time a [Source] is wanted.
 */
class NullSource: Source {
    override val id: Int = -1
    override val name: String = "Null Source"

    override fun fetchMangaInformation(manga: Manga): Observable<Manga> {
        throw Exception("Null Source called")
    }

}