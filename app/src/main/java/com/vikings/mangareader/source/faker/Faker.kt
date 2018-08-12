package com.vikings.mangareader.source.faker

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
            if (FakerFailure.isSuccess()) {
                it.onNext(MangasPage(
                    mangas = (0..MANGA_PER_PAGE).map { index ->
                        val manga = MangaImpl()
                        manga.name = "Manga ${index + MANGA_PER_PAGE * page} (page $page)"
                        manga.url = "fake url"
                        manga.sourceId = id
                        manga
                    },
                    hasNext = true
                ))
            }
            else {
                it.onError(Exception("Could not load faker mangas list"))
            }

            it.onComplete()
        }
    }

    override fun fetchMangaInformation(manga: Manga): Observable<Manga> {
        return Observable.create {
            if (FakerFailure.isSuccess()) {
                manga.status = Manga.Status.Finished
                manga.rating = 0.5f
                manga.authors = listOf("Author 1", "Another authors with long name", "3")
                manga.genres = listOf("Hello", "World", "I'm a genre!")
                manga.summary = "I'm a summary with a relatively short description of what the manga contains"

                it.onNext(manga)
            }
            else {
                it.onError(Exception("Could not load faker manga"))
            }
            it.onComplete()
        }
    }

}