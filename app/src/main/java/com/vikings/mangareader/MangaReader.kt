package com.vikings.mangareader

import android.app.Application
import com.vikings.mangareader.core.SourceManager
import com.vikings.mangareader.network.Network
import com.vikings.mangareader.source.Local
import com.vikings.mangareader.source.Mangakakalot
import com.vikings.mangareader.source.faker.Faker
import com.vikings.mangareader.storage.Storage

class MangaReader: Application() {
    override fun onCreate() {
        super.onCreate()

        Network.init(applicationContext)
        Storage.init(applicationContext)

        listOf(
            Local(applicationContext),
            Faker(),
            Mangakakalot()
        ).forEach { source -> SourceManager.add(source) }
    }
}