package com.vikings.mangareader.core

import android.util.Log
import java.io.Serializable
import java.util.*

/**
 * Contains all information relative to the chapter.
 *
 * Some fields are nullable as they can be empty if an
 * error occurs during information retrieval (network errors...),
 * and at some point, they can't be loaded yet.
 *
 * For all fields that are a list, an empty list means that there is nothing
 * in it (content loaded), whereas a null list means that the field is not
 * yet loaded (or an error occurred).
 */
interface Chapter: Serializable {
    var name: String

    /**
     * The source to which the chapter need to be loaded to load all wanted information.
     * This field is for loading, not how to get the original source.
     */
    var sourceId: Int
    /**
     * The url used to retrieve the original chapter information. It have multiple use:
     * - for website source, know where to load all chapter information.
     * - for local source, where the chapter can be loaded to have up-to-date information.
     *
     * The Chapter must not depend on it's [Manga] parent to load the content,
     * it must only use its own fields. Doing so allow to reduce coupling between
     * the [Manga] and its [Chapter]s
     */
    var url: String

    /**
     * The number of the chapter.
     * Used for efficient chapters sorting when needed.
     */
    var number: Float?

    /**
     * When the chapter have been released.
     */
    var release: Date?

    var pages: List<Page>?

    /**
     * To call when additional information is not needed anymore, only
     * a way to get back all the information again
     */
    fun dispose() {
        Log.i("Core", "chapter disposed")
        pages = null
    }
}