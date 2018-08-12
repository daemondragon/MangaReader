package com.vikings.mangareader.ui

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vikings.mangareader.R
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.SourceManager
import kotlinx.android.synthetic.main.fragment_manga.*


/**
 * A [Fragment] that display manga information.
 * Activities that contain this fragment must implement the
 * [MangaFragment.Listener] interface
 * to handle interaction events.
 * Use the [MangaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MangaFragment : Fragment() {
    companion object {
        const val MANGA = "MangaFragment.manga"

        @JvmStatic
        fun newInstance(manga: Manga): MangaFragment {
            return MangaFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MANGA, manga)
                }
            }
        }
    }

    private lateinit var manga: Manga

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            manga = it.getSerializable(MANGA) as Manga
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manga, container, false)
    }

    override fun onStart() {
        super.onStart()

        activity?.title = manga.name
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

    private fun loadManga() {
        manga_refresh.isRefreshing = true
        val source = SourceManager.get(manga.sourceId)
        source.fetchMangaInformation(manga)
            .subscribe({
                display(it)

                //TODO: load cover
                //TODO: load favorite and auto dl from db
                manga_refresh.isRefreshing = false
            },{
                manga_refresh.isRefreshing = false

                Snackbar.make(manga_coordinator,
                    R.string.error_mangas_list_load,
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
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface Listener {
        fun onChapterSelected(manga: Manga)
    }

    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement Listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

}
