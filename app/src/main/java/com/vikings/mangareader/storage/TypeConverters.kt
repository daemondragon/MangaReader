package com.vikings.mangareader.storage

import android.arch.persistence.room.TypeConverter
import com.vikings.mangareader.core.Manga
import java.util.*


class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(", ")
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(", ")
    }
}

class StatusConverter {
    @TypeConverter
    fun fromStatus(value: Manga.Status): Int {
        return when (value) {
            Manga.Status.OnGoing  -> 0
            Manga.Status.Finished -> 1
            Manga.Status.Licensed -> 2
            Manga.Status.Unknown  -> 3
        }
    }

    @TypeConverter
    fun fromInt(value: Int): Manga.Status {
        return when (value) {
            0 -> Manga.Status.OnGoing
            1 -> Manga.Status.Finished
            2 -> Manga.Status.Licensed
            3 -> Manga.Status.Unknown
            else -> Manga.Status.Unknown
        }
    }
}

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}
