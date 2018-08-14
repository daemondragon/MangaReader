package com.vikings.mangareader.storage

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(
    entities = [MangaEntity::class, ChapterEntity::class],
    version = 1
)

abstract class Database: RoomDatabase() {
    abstract fun mangaDao(): MangaDao

    abstract fun chapterDao(): ChapterDao
}