package com.vikings.mangareader.storage

import android.arch.persistence.room.Room
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.vikings.mangareader.core.Chapter
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.PageImpl
import java.io.File
import java.io.FileOutputStream

/**
 * Utility object that will save, get and remove manga.
 * It's not a source as the [Storage] is not responsible
 * to be ran in it's own thread to prevent UI handling.
 *
 * Doing so will be made in the local source.
 */
object Storage {
    private lateinit var directory: File
    private lateinit var database: Database

    fun init(context: Context) {
        directory = context.filesDir

        database = Room.databaseBuilder(context.applicationContext,
            Database::class.java, "mangas").build()
    }

    fun getDatabase(): Database {
        return database
    }

    /**
     * Save the manga information.
     * Doesn't save all contained chapters as the user may want to
     * only download a subset of them.
     */
    fun saveManga(manga: Manga) {
        database.mangaDao().insert(MangaEntity.from(manga))
    }

    fun saveChapter(manga: Manga, chapter: Chapter) {
        database.chapterDao().insert(ChapterEntity.from(manga, chapter))
    }

    fun saveCover(manga: Manga, cover: Drawable) {
        val mangaDirectory = File(directory, "${manga.sourceId}_${manga.name}")
        mangaDirectory.mkdirs()
        savePicture(mangaDirectory, cover, "cover.png")
    }

    /**
     * Save the page of the given chapter at the given index.
     * Both chapter.pages and page.picture must not be null.
     */
    fun savePage(manga: Manga, chapter: Chapter, pageIndex: Int) {
        val mangaDirectory = File(directory, "${manga.sourceId}_${manga.name}")
        val chapterDirectory = File(mangaDirectory, chapter.name)
        chapterDirectory.mkdirs()

        savePicture(chapterDirectory, chapter.pages!![pageIndex].picture!!, "$pageIndex.png")
    }

    private fun savePicture(directory: File, picture: Drawable, name: String) {
        val pictureFile = File(directory, name)
        val outStream = FileOutputStream(pictureFile)
        (picture as BitmapDrawable).bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
        outStream.flush()
        outStream.close()
    }

    fun deleteManga(mangaEntity: MangaEntity) {
        val mangaDirectory = File(directory, "${mangaEntity.sourceId}_${mangaEntity.name}")
        mangaDirectory.deleteRecursively()

        database.mangaDao().delete(mangaEntity)
    }

    fun deleteChapter(chapterEntity: ChapterEntity) {
        val mangaDirectory = File(directory, "${chapterEntity.originalSourceId}_${chapterEntity.mangaName}")
        val chapterDirectory = File(mangaDirectory, chapterEntity.name)
        chapterDirectory.deleteRecursively()

        database.chapterDao().delete(chapterEntity)
    }

    fun fetchChapters(mangaEntity: MangaEntity) {
        mangaEntity.chapters = database.chapterDao().all(mangaEntity.name, mangaEntity.originalSourceId)
    }

    /**
     * Set in the chapter it's pages list where each page contains in the url
     * the path to the picture.
     */
    fun fetchPages(chapterEntity: ChapterEntity) {
        val mangaDirectory = File(directory, "${chapterEntity.originalSourceId}_${chapterEntity.mangaName}")
        val chapterDirectory = File(mangaDirectory, chapterEntity.name)
        chapterEntity.pages = chapterDirectory.listFiles()
            .map { file ->
                val page = PageImpl(-1)
                page.url = file.path

                page
            }
    }
}