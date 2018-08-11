package com.vikings.mangareader.source

import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.MangasPage
import com.vikings.mangareader.core.Source
import io.reactivex.Observable

class Mangakakalot: Source {
    override val id: Int = 1

    override val name: String = "Mangakakalot"

    override fun fetchLatestMangas(page: Int): Observable<MangasPage> {
        TODO("not implemented")
    }

    override fun fetchMangaInformation(manga: Manga): Observable<Manga> {
        TODO("not implemented")
    }
}