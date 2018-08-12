package com.vikings.mangareader.core

import java.util.*

/**
 * See [Chapter] for fields explanation.
 */
class ChapterImpl: Chapter {
    override lateinit var name: String

    override var sourceId: Int = -1

    override lateinit var url: String

    override var number: Float? = null

    override var release: Date? = null

    override var pages: List<Page>? = null
}