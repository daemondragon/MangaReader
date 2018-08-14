package com.vikings.mangareader.source

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import com.vikings.mangareader.R
import com.vikings.mangareader.core.*
import com.vikings.mangareader.storage.ChapterEntity
import com.vikings.mangareader.storage.MangaEntity
import com.vikings.mangareader.storage.Storage
import io.reactivex.Observable
import kotlin.concurrent.thread


class Local(context: Context) : Source {
    override val id: Int = 0
    override val name: String = context.getString(R.string.local_storage)

    override fun fetchLatestMangas(page: Int): Observable<MangasPage> {
        return Observable.create { emitter ->
            val handler = Handler()
            thread {
                try {
                    val mangasPage = MangasPage(
                        mangas = Storage.getDatabase().mangaDao().all(),
                        hasNext = false
                    )
                    mangasPage.mangas.forEach { it.sourceId = id }

                    handler.post {
                        emitter.onNext(mangasPage)
                        emitter.onComplete()
                    }
                } catch (e: Exception) {
                    handler.post {
                        emitter.onError(e)
                        emitter.onComplete()
                    }
                }
            }
        }
    }

    override fun fetchMangaInformation(manga: Manga): Observable<Manga> {
        return Observable.create { emitter ->
            val handler = Handler()
            thread {
                try {
                    Storage.fetchChapters(manga as MangaEntity)

                    manga.chapters?.forEach { it.sourceId = id }

                    handler.post {
                        emitter.onNext(manga)
                        emitter.onComplete()
                    }
                } catch (e: Exception) {
                    handler.post {
                        emitter.onError(e)
                        emitter.onComplete()
                    }
                }
            }
        }
    }

    override fun fetchMangaCover(manga: Manga): Observable<Drawable> {
        return Observable.create { emitter ->
            try {
                val cover = Storage.fetchCover(manga as MangaEntity)
                if (cover != null)
                    emitter.onNext(cover)
                else
                    emitter.onError(Exception("Could not load manga cover"))
            } catch (e: Exception) {
                emitter.onError(e)
            } finally {
                emitter.onComplete()
            }
        }
    }

    override fun fetchChapterInformation(chapter: Chapter): Observable<Chapter> {
        return Observable.create { emitter ->
            val handler = Handler()
            thread {
                try {
                    Storage.fetchPages(chapter as ChapterEntity)

                    chapter.pages?.forEach {
                        it.sourceId = id
                        Log.i("Local", "chapter information: page ${it.url}")
                    }

                    handler.post {
                        emitter.onNext(chapter)
                        emitter.onComplete()
                    }

                } catch (e: Exception) {
                    handler.post {
                        emitter.onError(e)
                        emitter.onComplete()
                    }
                }
            }
        }
    }

    override fun fetchPageInformation(page: Page): Observable<Page> {
        return Observable.create { emitter ->
            try {
                val picture = Storage.fetchPage(page)
                if (picture != null) {
                    Log.i("Local", "page loaded: ${page.url}")
                    page.picture = picture
                    emitter.onNext(page)
                } else
                    emitter.onError(Exception("Could not load manga cover"))
            } catch (e: Exception) {
                emitter.onError(e)
            } finally {
                emitter.onComplete()
            }
        }
    }

}