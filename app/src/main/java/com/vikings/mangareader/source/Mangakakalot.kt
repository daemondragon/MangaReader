package com.vikings.mangareader.source

import android.graphics.drawable.Drawable
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.vikings.mangareader.core.*
import com.vikings.mangareader.network.Network
import com.vikings.mangareader.network.PictureRequest
import io.reactivex.Observable
import org.jsoup.Jsoup
import java.util.*

class Mangakakalot: Source {
    override val id: Int = 2
    override val name: String = "Mangakakalot"

    override fun fetchLatestMangas(page: Int): Observable<MangasPage> {
        val pageUrl = "http://mangakakalot.com/manga_list?type=latest&category=all&state=all&page=${page + 1}"
        return Observable.create { emitter ->
            Network.getInstance().addToRequestQueue(
                StringRequest(Request.Method.GET, pageUrl,
                    Response.Listener<String> { response ->
                        val document = Jsoup.parse(response)
                        val mangas = document.select(".list-truyen-item-wrap")
                            .map { element ->
                                val link = element.select("h3 > a")
                                val manga = MangaImpl(id)
                                manga.name = link.text()
                                manga.url = link.attr("href")

                                Log.i("Mangakakalot", "manga: \"${manga.name}\" with url: \"${manga.url}\"")

                                manga
                            }

                        val lastUrl = document
                            .select("div.group-page > a.page-blue:last-of-type")
                            .attr("href")

                        Log.i("Mangakakalot", "last url of page: $lastUrl")

                        emitter.onNext(MangasPage(
                            mangas = mangas,
                            hasNext = lastUrl != pageUrl
                        ))
                        emitter.onComplete()
                    },
                    Response.ErrorListener {
                        emitter.onError(Exception("Could not load latest mangas"))
                        emitter.onComplete()
                    }
                )
            )
        }
    }

    override fun fetchMangaInformation(manga: Manga): Observable<Manga> {
        return Observable.create { emitter ->
            Network.getInstance().addToRequestQueue(
                StringRequest(Request.Method.GET, manga.url,
                    Response.Listener<String> { html ->
                        val document = Jsoup.parse(html)

                        manga.coverUrl = document.select("div.manga-info-pic > img").attr("src")

                        val information = document.select("ul.manga-info-text")

                        manga.name     = information.select("li:eq(0) > h1").text()
                        manga.authors  = information.select("li:eq(1) > a").map { link -> link.text() }
                        manga.status   = parseStatus(information.select("li:eq(2)").text())
                        manga.genres   = information.select("li:eq(6) > a").map { link -> link.text() }
                        manga.rating   = information.select("li:eq(8) em[property=v:average]").text().toFloat() /
                            information.select("li:eq(8) em[property=v:best]").text().toFloat()

                        manga.summary  = document.select("div#noidungm").text()

                        manga.chapters = document.select("div#chapter div.chapter-list > div.row")
                            .map { element ->
                                val chapter = ChapterImpl(id)
                                chapter.name = element.select("span:eq(0) > a").text()
                                chapter.url = element.select("span:eq(0) > a").attr("href")
                                chapter.release = Date()//TODO: parse date

                                chapter
                            }

                        Log.i("manga information", "name: ${manga.name}")
                        Log.i("manga information", "coverUrl: ${manga.coverUrl}")
                        Log.i("manga information", "authors: ${manga.authors}")
                        Log.i("manga information", "genres: ${manga.genres}")
                        Log.i("manga information", "summary: ${manga.summary}")
                        Log.i("manga information", "rating: ${manga.rating}")

                        Log.i("manga information", "chapters: ${manga.chapters!!.map { "name: \"${it.name}\", number: ${it.number}" }}")

                        emitter.onNext(manga)
                        emitter.onComplete()
                    },
                    Response.ErrorListener {
                        emitter.onError(Exception("Could not load latest mangas"))
                        emitter.onComplete()
                    }
                )
            )
        }
    }

    private fun parseStatus(text: String): Manga.Status {
        return when {
            text.contains("Ongoing")   -> Manga.Status.OnGoing
            text.contains("Completed") -> Manga.Status.Finished
            text.contains("Licensed")  -> Manga.Status.Licensed
            else                             -> Manga.Status.Unknown
        }
    }

    override fun fetchMangaCover(manga: Manga): Observable<Drawable> {
        return Observable.create { emitter ->
            Network.getInstance().addToRequestQueue(
                PictureRequest(manga.coverUrl!!,
                    Response.Listener { picture ->
                        Log.i("manga cover", "${manga.coverUrl}")

                        emitter.onNext(picture)
                        emitter.onComplete()
                    },
                    Response.ErrorListener {
                        emitter.onError(Exception("Could not load manga cover"))
                        emitter.onComplete()
                    }
                )
            )
        }
    }

    override fun fetchChapterInformation(chapter: Chapter): Observable<Chapter> {
        return Observable.create { emitter ->
            Network.getInstance().addToRequestQueue(
                StringRequest(Request.Method.GET,
                    chapter.url,
                    Response.Listener<String> { response ->
                        chapter.pages = Jsoup.parse(response)
                            .select("img.img_content")
                            .map { element ->
                                val page = PageImpl(id)
                                page.url = element.attr("src")

                                Log.i("chapter information", "page \"${page.url}\"")

                                page
                            }

                        emitter.onNext(chapter)
                        emitter.onComplete()
                    },
                    Response.ErrorListener {
                        emitter.onError(Exception("Could not load chapter information"))
                        emitter.onComplete()
                    }
                )
            )
        }
    }

    override fun fetchPageInformation(page: Page): Observable<Page> {
        return Observable.create { emitter ->
            Network.getInstance().addToRequestQueue(
                PictureRequest(page.url,
                    Response.Listener { picture ->
                        page.picture = picture
                        emitter.onNext(page)
                        emitter.onComplete()
                    },
                    Response.ErrorListener {
                        emitter.onError(Exception("Could not load page"))
                        emitter.onComplete()
                    }
                )
            )
        }
    }

}