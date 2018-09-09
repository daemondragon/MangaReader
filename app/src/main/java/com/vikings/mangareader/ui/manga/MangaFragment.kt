package com.vikings.mangareader.ui.manga

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vikings.mangareader.R
import com.vikings.mangareader.core.Chapter
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.SourceManager
import com.vikings.mangareader.viewmodels.MangaViewModel
import com.vikings.mangareader.viewmodels.MangaViewModelFactory
import kotlinx.android.synthetic.main.fragment_manga.*

class MangaFragment : Fragment() {
    companion object {
        private const val MANGA = "MangaFragment.manga"

        fun newInstance(manga: Manga): MangaFragment {
            return MangaFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MANGA, manga)
                }
            }
        }
    }

    private lateinit var chaptersListAdapter: ChaptersListAdapter
    private lateinit var manga: Manga
    private lateinit var mangaViewModel: MangaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            manga = this.getSerializable(MANGA) as Manga
            chaptersListAdapter = ChaptersListAdapter(manga)
            mangaViewModel = ViewModelProviders.of(this@MangaFragment, MangaViewModelFactory(manga))
                .get(MangaViewModel::class.java)
        }
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

        mangaViewModel.getManga().observe(this,
            Observer { manga -> if (manga != null) display(manga) })
        mangaViewModel.getMangaCover().observe(this,
            Observer { picture -> manga_cover.setImageDrawable(picture) })

        mangaViewModel.getErrors().observe(this,
            Observer { error ->
                if (error != null) {
                    Snackbar.make(manga_coordinator,
                        R.string.error_manga_load,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry) { _ ->
                            mangaViewModel.errorHandled()
                            mangaViewModel.fetchMangaInformation()
                        }
                        .show()
                }
            })

        mangaViewModel.getLoadingState().observe(this,
            Observer { loadingState -> manga_refresh.isRefreshing = loadingState!! })
    }

    override fun onResume() {
        super.onResume()

        activity?.title = manga.name
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