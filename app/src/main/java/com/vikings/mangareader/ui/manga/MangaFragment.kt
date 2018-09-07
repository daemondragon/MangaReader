package com.vikings.mangareader.ui.manga

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vikings.mangareader.R
import com.vikings.mangareader.core.Chapter
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.SourceManager
import kotlinx.android.synthetic.main.fragment_manga.*

class MangaFragment : Fragment() {
    companion object {
        const val MANGA = "MangaFragment.manga"

        fun newInstance(manga: Manga): MangaFragment {
            return MangaFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MANGA, manga)
                }
            }
        }
    }

    lateinit var manga: Manga

    private val chaptersListAdapter = ChaptersListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply { manga = this.getSerializable(MANGA) as Manga }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manga, container, false)
    }

    override fun onStart() {
        super.onStart()

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

                chaptersListAdapter.listener = listener//TODO: hack, find a nicer ways
                //Display all chapters
                chaptersListAdapter.chapters.clear()
                chaptersListAdapter.chapters.addAll(manga.chapters!!)
                chaptersListAdapter.notifyDataSetChanged()
            }
        }
    }

    private var listener: Listener? = null

    interface Listener {
        /**
         * When a chapter is selected in the chapters list
         */
        fun onChapterSelected(chapters: List<Chapter>, position: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement MangasListFragment.Listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}