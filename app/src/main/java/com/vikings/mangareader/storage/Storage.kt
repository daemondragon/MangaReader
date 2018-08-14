package com.vikings.mangareader.storage

import android.arch.persistence.room.Room
import android.content.Context

object Storage {
    private lateinit var database: Database

    fun init(context: Context) {
        database = Room.databaseBuilder(context.applicationContext,
            Database::class.java, "mangas").build()
    }
}