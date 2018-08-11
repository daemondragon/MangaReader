package com.vikings.mangareader.source

import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.MangaImpl
import com.vikings.mangareader.core.MangasPage
import com.vikings.mangareader.core.Source
import io.reactivex.Observable

/**
 * Fake source whose only purpose is to be used for tests.
 */
class Faker: Source {
    override val id: Int = 1
    override val name: String = "Faker"

    private val MANGA_PER_PAGE = 20

    override fun fetchLatestMangas(page: Int): Observable<MangasPage> {
        return Observable.create {
            it.onNext(MangasPage(
                mangas = (0..MANGA_PER_PAGE).map { index ->
                    val manga = MangaImpl()
                    manga.name = "Manga ${index + MANGA_PER_PAGE * page} (page $page)"
                    manga.url = "fake url"
                    manga
                },
                hasNext = true
            ))
            it.onComplete()
        }
    }

    override fun fetchMangaInformation(manga: Manga): Observable<Manga> {
        TODO("not implemented")
    }

}