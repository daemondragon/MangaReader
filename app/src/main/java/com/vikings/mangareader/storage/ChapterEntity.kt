package com.vikings.mangareader.storage

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import com.vikings.mangareader.core.Chapter
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.Page
import java.util.*

/**
 * A [ChapterEntity] is considered the same as another one
 * if they share the same manga (See [MangaEntity] to know what is "same")
 * and if they have the same name, as one each source, a manga chapter
 * is generally unique in its manga.
 */
@Entity(
    primaryKeys = ["name", "mangaName", "originalSourceId"],
    tableName = "chapters"
)
@ForeignKey(entity = MangaEntity::class,
    parentColumns = ["name", "originalSourceId"],
    childColumns = ["mangaName", "originalSourceId"])
class ChapterEntity: Chapter {
    /**
     * Used to have a link to the [MangaEntity]
     */
    lateinit var mangaName: String

    override lateinit var name: String

    @Ignore
    override var sourceId: Int = -1

    /**
     * The original source used to get the chapter.
     * Both used to know the original source of the chapter
     * and as a link to the [MangaEntity], as a manga and its chapters
     * shares the same [originalSourceId]
     */
    var originalSourceId: Int = -1

    override lateinit var url: String

    override var number: Float? = null

    override var release: Date? = null

    @Ignore//Page will be retrieved differently
    override var pages: List<Page>? = null

    companion object {
        fun from(manga: Manga, chapter: Chapter): ChapterEntity {
            val chapterEntity = ChapterEntity()

            chapterEntity.mangaName = manga.name
            chapterEntity.name = chapter.name
            chapterEntity.originalSourceId = chapter.sourceId
            chapterEntity.url = chapter.url
            chapterEntity.number = chapter.number
            chapterEntity.release = chapter.release

            return chapterEntity
        }
    }
}