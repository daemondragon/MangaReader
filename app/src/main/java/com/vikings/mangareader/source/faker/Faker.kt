package com.vikings.mangareader.source.faker

import android.graphics.drawable.Drawable
import com.vikings.mangareader.core.*
import io.reactivex.Observable
import java.util.*

/**
 * Fake source whose only purpose is to be used for tests.
 */
class Faker: Source {
    override val id: Int = 1
    override val name: String = "Faker"

    private val MANGAS_PER_PAGE    = 20
    private val CHAPTERS_PER_MANGA = 10
    private val PAGES_PER_CHAPTER  = 20

    override fun getCategories(): List<Pair<String, String>> {
        return listOf(Pair("Category 1", "1"), Pair("Category 2", "2"), Pair("Category 3", "3"))
    }

    override fun fetchMangasBy(categoryKey: String, page: Int): Observable<MangasPage> {
        return Observable.create {
            if (FakerFailure.isSuccess()) {
                it.onNext(MangasPage(
                    mangas = (0..MANGAS_PER_PAGE).map { index ->
                        val manga = MangaImpl(id)
                        manga.name = "Manga ${index + MANGAS_PER_PAGE * page} (page $page) (category $categoryKey)"
                        manga.url = "fake url"
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

    override fun fetchSearch(mangaName: String, page: Int): Observable<MangasPage> {
        TODO("not implemented")
    }

    override fun fetchMangaInformation(manga: Manga): Observable<Manga> {
        return Observable.create {
            if (FakerFailure.isSuccess()) {
                manga.status = Manga.Status.Finished
                manga.rating = 0.5f
                manga.authors = listOf("Author 1", "Another authors with long name", "3")
                manga.genres = listOf("Hello", "World", "I'm a genre!")
                manga.summary = "I'm a summary with a relatively short description of what the manga contains"
                manga.chapters = (0..CHAPTERS_PER_MANGA).map { index ->
                    val chapter = ChapterImpl(id)
                    chapter.name = "Chapter $index"
                    chapter.url = "fake url"
                    chapter.release = Date()
                    chapter.number = index.toFloat()

                    chapter
                }

                it.onNext(manga)
            }
            else {
                it.onError(Exception("Could not load faker manga"))
            }
            it.onComplete()
        }
    }

    override fun fetchMangaCover(manga: Manga): Observable<Drawable> {
        return Observable.create {
            //Do not load a drawable as it is hard to get one from resource directory
            //without the current context.
            it.onError(Exception("Could not load faker cover"))
            it.onComplete()
        }
    }

    override fun fetchChapterInformation(chapter: Chapter): Observable<Chapter> {
        return Observable.create {
            if (FakerFailure.isSuccess()) {

                chapter.number = chapter.number ?: 0.0f
                chapter.release = chapter.release ?: Date()
                chapter.pages = (0..PAGES_PER_CHAPTER).map { _ ->
                    val page = PageImpl(id)
                    page.url = "fake url"

                    page
                }

                it.onNext(chapter)
            }
            else {
                it.onError(Exception("Could not load faker chapter"))
            }
            it.onComplete()
        }
    }

    override fun fetchPagePicture(page: Page): Observable<Drawable> {
        return Observable.create {
            it.onError(Exception("Could not load faker page"))
            it.onComplete()
        }
    }
}