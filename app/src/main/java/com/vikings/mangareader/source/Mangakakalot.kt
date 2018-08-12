package com.vikings.mangareader.source

import android.graphics.drawable.Drawable
import android.text.Html
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.vikings.mangareader.core.*
import com.vikings.mangareader.network.Network
import com.vikings.mangareader.network.PictureRequest
import io.reactivex.Observable
import java.util.*

class Mangakakalot: Source {
    override val id: Int = 2
    override val name: String = "Mangakakalot"

    override fun fetchLatestMangas(page: Int): Observable<MangasPage> {
        return Observable.create { emitter ->
            Network.getInstance().addToRequestQueue(
                StringRequest(Request.Method.GET,
                    "http://mangakakalot.com/manga_list?type=latest&category=all&state=all&page=$page",
                    Response.Listener<String> { response ->
                        emitter.onNext(MangasPage(
                            mangas = response
                                .split("list-truyen-item-wrap")
                                .drop(1)//Remove trash data before first element
                                .map { html ->
                                    val startPos = html.indexOf("<h3>")
                                    val startUrl = html.indexOf("href=\"", startPos) + "href=\"".length
                                    val endUrl = html.indexOf("\"", startUrl)
                                    val startName = html.indexOf(">", endUrl) + ">".length
                                    val endName = html.indexOf("</a>", startName)

                                    if (startPos < 0 || startUrl < 0 || endUrl < 0 || startName < 0 || endName < 0) {
                                        emitter.onError(Exception("Could not parse latest mangas"))
                                        emitter.onComplete()
                                        return@Listener
                                    }

                                    val manga = MangaImpl(id)
                                    manga.name = html.substring(startName, endName)
                                    manga.url = html.substring(startUrl, endUrl)

                                    manga
                                },
                            hasNext = false
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
                        val startPos   = html.indexOf("manga-info-top")
                        val startCover = html.indexOf("<img src=\"", startPos) + "<img src=\"".length
                        val endCover   = html.indexOf("\"", startCover)
                        val startName  = html.indexOf("<h1>", endCover) + "<h1>".length
                        val endName    = html.indexOf("</h1>", startName)

                        val startAuthors = html.indexOf("<li>", endName) + "<li>".length
                        val endAuthors   = html.indexOf("</li>", startAuthors)
                        val startGenres  = html.indexOf("Genres", endAuthors)
                        val endGenres    = html.indexOf("</li>", startGenres)

                        val startSummary = html.indexOf("</h2>", endGenres) + "</h2>".length
                        val endSummary   = html.indexOf("</div>", startSummary)
                        val startChapter = html.indexOf("chapter-list", endSummary)
                        val endChapter   = html.indexOf("class=\"comment-info", startChapter)

                        //Checking for parsing error
                        if (startPos < 0 || startCover < 0 || endCover < 0 || startName < 0 || endName < 0 ||
                            startAuthors < 0 || endAuthors < 0 || startGenres < 0 || endGenres < 0 ||
                            startSummary < 0 || endSummary < 0 || startChapter < 0 || endChapter < 0) {
                            emitter.onError(Exception("Could not parse manga information"))
                            emitter.onComplete()
                            return@Listener
                        }

                        //Parsing both authors and genres list
                        val authors = html.substring(startAuthors, endAuthors)
                            .split("<a").drop(1)
                            .map { subHtml ->
                                val start = subHtml.indexOf(">") + ">".length
                                val end = subHtml.indexOf("<", start)
                                if (start < 0 || end < 0) {
                                    emitter.onError(Exception("Could not parse manga information"))
                                    emitter.onComplete()
                                    return@Listener
                                }
                                subHtml.substring(start, end)
                            }
                        val genres = html.substring(startGenres, endGenres)
                            .split("<a").drop(1)
                            .map { subHtml ->
                                val start = subHtml.indexOf(">") + ">".length
                                val end = subHtml.indexOf("<", start)
                                if (start < 0 || end < 0) {
                                    emitter.onError(Exception("Could not parse manga information"))
                                    emitter.onComplete()
                                    return@Listener
                                }
                                subHtml.substring(start, end)
                            }

                        val chapters = html.substring(startChapter, endChapter)
                            .split("<a").drop(1)
                            .map { subHtml ->
                                val startUrl = subHtml.indexOf("href=\"") + "href=\"".length
                                val endUrl   = subHtml.indexOf("\"", startUrl)
                                val startChapterName = subHtml.indexOf(">", endUrl) + ">".length
                                val endChapterName   = subHtml.indexOf("<", startChapterName)
                                val startRelease     = subHtml.indexOf("<span>",
                                    (subHtml.indexOf("<span>", endChapterName) + "<span>".length)
                                ) + "<span>".length
                                val endRelease       = subHtml.indexOf("<", startRelease)

                                if (startUrl < 0 || endUrl < 0 || startChapterName < 0 || endChapterName < 0
                                    || startRelease < 0 || endRelease < 0) {
                                    emitter.onError(Exception("Could not parse manga information"))
                                    emitter.onComplete()
                                    return@Listener
                                }


                                val chapter =ChapterImpl(id)
                                chapter.name = subHtml.substring(startChapterName, endChapterName)
                                chapter.release = Date()//subHtml.substring(startRelease, endRelease),
                                chapter.url = subHtml.substring(startUrl, endUrl)

                                //TODO: parse chapter number and release
                                chapter
                            }

                        manga.name     = html.substring(startName, endName)
                        manga.summary  = Html.fromHtml(html.substring(startSummary, endSummary)).toString()
                        manga.authors  = authors
                        manga.genres   = genres
                        manga.coverUrl = html.substring(startCover, endCover)
                        manga.chapters = chapters

                        //TODO: parse manga rating and status

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

    override fun fetchMangaCover(manga: Manga): Observable<Drawable> {
        return Observable.create { emitter ->
            Network.getInstance().addToRequestQueue(
                PictureRequest(manga.coverUrl!!,
                    Response.Listener { picture ->
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
                        chapter.pages = response
                            .split("img_content")
                            .dropLast(1)//Remove the last element (url is BEFORE each element)
                            .map { html ->
                                val start = html.lastIndexOf("src=\"") + "src=\"".length
                                val end   = html.indexOf("\"", start)

                                if (start < 0 || end < 0) {
                                    emitter.onError(Exception("Could not load chapter information"))
                                    emitter.onComplete()
                                    return@Listener
                                }

                                val page = PageImpl(id)
                                page.url = html.substring(start, end)
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