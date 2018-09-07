package com.vikings.mangareader.storage

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

@Database(
    entities = [MangaEntity::class, ChapterEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(StringListConverter::class, StatusConverter::class, DateConverter::class)
abstract class Database: RoomDatabase() {
    abstract fun mangaDao(): MangaDao

    abstract fun chapterDao(): ChapterDao
}