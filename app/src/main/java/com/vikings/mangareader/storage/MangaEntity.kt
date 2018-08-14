package com.vikings.mangareader.storage

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import com.vikings.mangareader.core.Chapter
import com.vikings.mangareader.core.Manga

/**
 * A [Manga] that will be stored on the user local storage for offline reading
 * and automatic download.
 *
 * A [Manga] primary keys are it's name and it's original source id as
 * names are generally unique for each source.
 * Doing so means that the "same" manga (from the user point-of-view)
 * will have two entries in the database if they are marked as favorite
 * from two different source.
 */
@Entity(
    primaryKeys = ["name", "originalSourceId"],
    tableName = "mangas"
)
class MangaEntity: Manga {
    override lateinit var name: String

    /**
     * Ignored as it's the source that will set it's id, not the database.
     */
    @Ignore
    override var sourceId: Int = -1

    /**
     * The original source used to get the manga.
     * Used so that automatic download can know from which
     * source the download need to be done.
     */
    var originalSourceId: Int = -1

    override lateinit var url: String

    override var coverUrl: String? = null

    override var summary: String? = null

    override var authors: List<String>? = null

    override var genres: List<String>? = null

    override var rating: Float? = null

    override var status: Manga.Status = Manga.Status.Unknown

    /**
     * Ignored as they will be retrieved differently
     * To retrieve chapters, use the foreign key in the chapter
     * of the current manga.
     */
    @Ignore
    override var chapters: List<Chapter>? = null

    override var favorite: Boolean? = null

    override var automaticDownload: Boolean? = null

    companion object {
        fun from(manga: Manga): MangaEntity {
            val mangaEntity = MangaEntity()

            mangaEntity.name = manga.name
            mangaEntity.originalSourceId = manga.sourceId
            mangaEntity.url = manga.url
            mangaEntity.coverUrl = manga.coverUrl
            mangaEntity.summary = manga.summary
            mangaEntity.authors = manga.authors
            mangaEntity.genres = manga.genres
            mangaEntity.rating = manga.rating
            mangaEntity.status = manga.status
            mangaEntity.favorite = manga.favorite
            mangaEntity.automaticDownload = manga.automaticDownload

            return mangaEntity
        }
    }
}