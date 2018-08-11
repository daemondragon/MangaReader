package com.vikings.mangareader.source

import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.Source
import io.reactivex.Observable

class Mangakakalot: Source {
    override val id: Int = 1

    override val name: String = "Mangakakalot"

    override fun fetchMangaInformation(manga: Manga): Observable<Manga> {
        TODO("not implemented")
    }

}