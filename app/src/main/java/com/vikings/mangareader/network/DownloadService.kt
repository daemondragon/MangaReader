package com.vikings.mangareader.network

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.vikings.mangareader.R
import com.vikings.mangareader.core.Chapter
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.SourceManager
import com.vikings.mangareader.storage.ChapterEntity
import com.vikings.mangareader.storage.Storage
import kotlin.concurrent.thread

/**
 * An [IntentService] subclass for handling background manga download and manga deletion
 * from storage.
 *
 * Downloaded page are saved from left to right and in this order.
 * It allows in case of download failure to easily get the next page to
 * download without having to overwrite previous one.
 */
class DownloadService : IntentService("DownloadService") {
    override fun onHandleIntent(intent: Intent?) {
        //Create now as all action want to use notification
        //(it's a foreground service)
        createNotificationChannel()

        when (intent?.action) {
            DOWNLOAD -> {
                val chapter = intent.extras?.getSerializable(CHAPTER) as Chapter
                val manga = intent.extras?.getSerializable(MANGA) as Manga
                val id = intent.extras?.getInt(NOTIFICATION_ID, notificationIdCounter++)
                download(manga, chapter, id!!)
            }
            DELETE -> {
                delete(intent.extras?.getSerializable(CHAPTER) as ChapterEntity)
            }
        }
    }

    private fun download(manga: Manga, chapter: Chapter, notificationId: Int) {
        val request = DownloadRequest(
            NotificationManagerCompat.from(this),
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle(chapter.name)
                .setPriority(NotificationCompat.PRIORITY_LOW),
            notificationId,
            manga,
            chapter,
            0
        )

        request.displayNotification()

        SourceManager.get(chapter.sourceId)
            .fetchChapterInformation(chapter)
            .subscribe({
                thread {
                    Storage.saveManga(manga)
                    Storage.saveChapter(manga, chapter)
                }.join()

                //TODO: Resume download where it stopped.
                request.progress = 0
                request.updateProgress()
                request.displayNotification()

                loadPage(request)
            },
            {
                it.printStackTrace()
                //Full request will be resend to the service
                request.relaunchRequest(this, R.string.error_chapter_load)
            })

        SourceManager.get(chapter.sourceId)
            .fetchMangaCover(manga)
            .subscribe(
            { picture ->
                thread { Storage.saveCover(manga, picture) }.join()
            },
            { /* Do nothing, as there is no real problem if the manga cover isn't stored */ }
        )
    }

    private fun loadPage(request: DownloadRequest) {
        if (request.progress >= request.chapter.pages!!.size) {
            request.cancelNotification()//Finished download
        }
        else {
            val source = SourceManager.get(request.chapter.sourceId)
            source.fetchPageInformation(request.chapter.pages!![request.progress]).subscribe(
                {
                    //Save picture
                    thread {
                        Storage.savePage(request.manga, request.chapter, request.progress)
                    }.join()

                    ++request.progress
                    request.updateProgress()
                    request.displayNotification()

                    loadPage(request)
                },
                {
                    //Full request will be resend to the service
                    request.relaunchRequest(this, R.string.error_page_load)
                }
            )
        }
    }

    private fun delete(chapter: ChapterEntity) {
        //TODO: Also delete manga if the manga doesn't contains chapters anymore.
        thread {
            Storage.deleteChapter(chapter)
        }.join()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_LOW)
            channel.description = getString(R.string.channel_description)

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    /**
     * Helper for easier service requests call
     * A manga is needed for both function so that manga information
     * can be stored if not already present, or just to know the manga name
     */
    companion object {
        //Used so that in case of request retry, the old notification is reused
        //instead of creating a new one.
        internal const val NOTIFICATION_ID = "notification"

        internal const val CHANNEL_ID = "download"

        internal const val DOWNLOAD = "download"
        internal const val DELETE   = "delete"

        internal const val MANGA    = "manga"
        internal const val CHAPTER  = "chapter"

        private var notificationIdCounter = 0

        fun download(context: Context, manga: Manga, chapter: Chapter) {
            val intent = Intent(context, DownloadService::class.java)
            intent.action = DOWNLOAD
            intent.putExtra(MANGA, manga)
            intent.putExtra(CHAPTER, chapter)

            context.startService(intent)
        }

        fun delete(context: Context, chapter: ChapterEntity) {
            val intent = Intent(context, DownloadService::class.java)
            intent.action = DELETE
            intent.putExtra(CHAPTER, chapter)

            context.startService(intent)
        }
    }
}

private data class DownloadRequest(
    val notificationManager: NotificationManagerCompat,
    val notification: NotificationCompat.Builder,
    var notificationId: Int,
    val manga: Manga,
    val chapter: Chapter,
    var progress: Int
) {
    fun updateProgress() {
        if (chapter.pages != null) {
            notification
                .setContentText("$progress/${chapter.pages!!.size}")
                .setProgress(chapter.pages!!.size, progress, false)
        }
    }
    fun displayNotification() {
        notificationManager.notify(notificationId, notification.build())
    }

    fun cancelNotification() {
        notificationManager.cancel(notificationId)
    }

    fun relaunchRequest(context: Context, reason: Int) {
        val intent = Intent(context, DownloadService::class.java)
        intent.action = DownloadService.DOWNLOAD
        intent.putExtra(DownloadService.CHAPTER, chapter)
        intent.putExtra(DownloadService.MANGA, manga)
        intent.putExtra(DownloadService.NOTIFICATION_ID, notificationId)

        notification
            .setProgress(0, 0, false)
            .setContentText("${context.getString(R.string.error)} ${context.getString(reason)}")
            .setAutoCancel(true)
            .addAction(R.drawable.ic_retry, context.getString(R.string.retry),
                PendingIntent.getService(context,
                    notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT))
        displayNotification()
    }
}