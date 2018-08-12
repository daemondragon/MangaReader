package com.vikings.mangareader.ui.manga

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import com.vikings.mangareader.R
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.SourceManager
import com.vikings.mangareader.ui.DrawerActivity
import com.vikings.mangareader.ui.page.PageActivity
import kotlinx.android.synthetic.main.activity_manga.*


class MangaActivity : DrawerActivity() {
    override fun getLayout(): Int = R.layout.activity_manga

    companion object {
        const val MANGA = "MangaActivity.manga"

        @JvmStatic
        fun getIntent(context: Context, manga: Manga): Intent {
            val intent = Intent(context, MangaActivity::class.java)
            intent.putExtra(MANGA, manga)
            return intent
        }
    }

    private lateinit var manga: Manga

    private val chaptersListAdapter = ChaptersListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manga = intent.extras?.getSerializable(MANGA) as Manga

        title = manga.name
        //Switch from the chapters list to the summary
        chapters_summary_switch.setOnCheckedChangeListener { _, checked ->
            if (!checked)
                chapters_summary.showPrevious()
            else
                chapters_summary.showNext()
        }
        manga_refresh.isEnabled = false//No user interaction
        loadManga()
    }

    override fun onDestroy() {
        super.onDestroy()
        manga.dispose()//To clear chapter information
    }

    private fun loadManga() {
        manga_refresh.isRefreshing = true
        val source = SourceManager.get(manga.sourceId)

        Log.i("Manga", "loading manga information")

        source.fetchMangaInformation(manga)
            .subscribe({ manga ->
                display(manga)

                source.fetchMangaCover(manga)
                    .subscribe(
                        { picture -> manga_cover.setImageDrawable(picture)},
                        { /* Do nothing ins case of cover loading error */})

                //TODO: load favorite and auto dl from db

                manga_refresh.isRefreshing = false
            },{
                manga_refresh.isRefreshing = false

                Snackbar.make(manga_coordinator,
                    R.string.error_manga_load,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry) { _ -> loadManga() }
                    .show()
            })
    }

    private fun display(manga: Manga) {
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

        manga_source.text = SourceManager.get(manga.sourceId).name

        if (manga.chapters != null) {
            chapters_list.apply {
                adapter = chaptersListAdapter
                //Display all chapters
                chaptersListAdapter.chapters.addAll(manga.chapters!!)
                chaptersListAdapter.notifyDataSetChanged()

                setOnItemClickListener { _, _, i, _ ->
                    startActivity(
                        PageActivity.getIntent(this@MangaActivity, manga.chapters!!, i))
                }
            }
        }
    }
}
