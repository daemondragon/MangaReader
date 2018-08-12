package com.vikings.mangareader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.SourceManager
import kotlinx.android.synthetic.main.activity_manga.*

class MangaActivity : AppCompatActivity() {
    companion object {
        const val MANGA = "MangaActivity.manga"
    }

    private lateinit var manga: Manga

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manga)

        manga = intent.extras?.getSerializable(MANGA) as Manga
        title = manga.name

        //Switch from the chapters list to the summary
        chapters_summary_switch.setOnCheckedChangeListener { _, checked ->
            if (!checked)
                chapters_summary.showPrevious()
            else
                chapters_summary.showNext()
        }

        loadManga()
    }

    private fun loadManga() {
        val source = SourceManager.get(manga.sourceId)
        source.fetchMangaInformation(manga)
            .subscribe({
                if (manga.authors != null)
                    manga_authors.text = manga.authors!!.joinToString()
                if (manga.genres != null)
                    manga_genres.text = manga.genres!!.joinToString()
                if (manga.rating != null)
                    manga_rating.text = "${manga.rating!!}"

                manga_status.text = getString(when (manga.status) {
                    Manga.Status.Unknown  -> R.string.status_unknown
                    Manga.Status.OnGoing  -> R.string.status_ongoing
                    Manga.Status.Finished -> R.string.status_finished
                    Manga.Status.Licensed -> R.string.status_licensed
                })

                if (manga.summary != null)
                    manga_summary.text = manga.summary

                manga_source.text = source.name

            },{
                TODO("add error handling")
            })
    }
}
