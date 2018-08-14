package com.vikings.mangareader.storage

import android.arch.persistence.room.*

/**
 * Retrieve chapter information
 */
@Dao
interface ChapterDao {
    @Query("SELECT * from chapters WHERE mangaName = :mangaName AND originalSourceId = :originalSourceId")
    fun all(mangaName: String, originalSourceId: Int): List<ChapterEntity>

    @Query("SELECT * from chapters WHERE mangaName = :mangaName AND originalSourceId = :originalSourceId AND name = :chapterName")
    fun get(mangaName: String, originalSourceId: Int, chapterName: String): ChapterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chapter: ChapterEntity)

    @Update
    fun update(chapter: ChapterEntity)

    @Delete
    fun delete(chapter: ChapterEntity)
}