package com.vikings.mangareader

import android.content.Context
import com.vikings.mangareader.core.SourceManager
import com.vikings.mangareader.source.faker.Faker

/**
 * Init all singleton that the application
 * need in order to function correctly.
 */
object Singletons {
    private var initialized = false

    fun initAll(context: Context) {
        if (initialized)
            return
        initialized = true

        SourceManager.add(Faker())
    }
}