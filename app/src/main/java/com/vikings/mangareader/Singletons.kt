package com.vikings.mangareader

import android.content.Context
import com.vikings.mangareader.core.SourceManager
import com.vikings.mangareader.network.Network
import com.vikings.mangareader.source.Mangakakalot
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

        Network.init(context)

        listOf(
            Faker(),
            Mangakakalot()
        ).forEach { source -> SourceManager.add(source) }
    }
}