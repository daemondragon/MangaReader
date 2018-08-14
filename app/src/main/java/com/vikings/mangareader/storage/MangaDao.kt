package com.vikings.mangareader.storage

import android.arch.persistence.room.*

/**
 * Retrieve manga information
 */
@Dao
interface MangaDao {
    @Query("SELECT * from mangas")
    fun all(): List<MangaEntity>

    @Query("SELECT * from mangas WHERE name = :mangaName AND originalSourceId = :originalSourceId")
    fun get(mangaName: String, originalSourceId: Int): MangaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(manga: MangaEntity)

    @Update
    fun update(manga: MangaEntity)

    @Delete
    fun delete(manga: MangaEntity)
}