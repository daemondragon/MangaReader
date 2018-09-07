package com.vikings.mangareader.core

import java.io.Serializable

/**
 * Contains all information relative to the chapter.
 *
 * Some fields are nullable as they can be empty if an
 * error occurs during information retrieval (network errors...),
 * and at some point, they can't be loaded yet.
 */
interface Page: Serializable {
    /**
     * The source to which the page need to be loaded.
     * This field is for loading, not how to get the original source.
     */
    var sourceId: Int

    /**
     * The url used to retrieve the original page.
     * The Chapter must not depend on it's [Chapter] parent to load the content,
     * it must only use its own fields. Doing so allow to reduce coupling between
     * the [Chapter] and its [Page]s
     */
    var url: String
}