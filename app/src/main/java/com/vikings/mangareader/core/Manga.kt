package com.vikings.mangareader.core

import java.io.Serializable

/**
 * Contains all information relative to the manga.
 *
 * Some fields are nullable as they can be empty if an
 * error occurs during information retrieval (network errors...),
 * and at some point, they can't be loaded yet.
 *
 * For all fields that are a list, an empty list means that there is nothing
 * in it (content loaded), whereas a null list means that the field is not
 * yet loaded (or an error occurred).
 */
interface Manga: Serializable {
    var name: String

    /**
     * The source to which the manga need to be loaded to load all wanted information.
     * This field is for loading, not how to get the original source.
     */
    var sourceId: Int
    /**
     * The url used to retrieve the original manga information. It have multiple use:
     * - for website source, know where to load all manga information.
     * - for local source, where the manga can be loaded to have up-to-date information.
     */
    var url: String

    var coverUrl: String?

    var summary: String?

    var authors: List<String>?

    var genres: List<String>?
    /**
     * Rating of the manga, between 0.0 and 1.0 (both included).
     */
    var rating: Float?
    /**
     * Manga current status. If not already loaded, use [Status.Unknown]
     */
    var status: Status

    /*
     * Under this comment all fields are fields that need to be loaded from
     * the database (local source) to know their values.
     *
     * Putting those fields here an not in a subclass allows for every source
     * (including website) to know if a manga have been marked as favorite or
     * have been downloaded for instance.
     */

    /**
     * If true, new chapters of this manga will be periodically checked,
     * and in function of [automaticDownload], will do the appropriate action.
     */
    var favorite: Boolean?
    /**
     * When a new chapter of this manga is released (and [favorite] is True):
     * if True: download the chapter automatically
     * if False: a Notification will be sent to the user to let him know of the release.
     * TODO: do those features
     */
    var automaticDownload: Boolean?

    /**
     * All possible status of a manga.
     */
    enum class Status {
        Unknown,
        OnGoing,
        Finished,
        Licensed
    }
}