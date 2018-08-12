package com.vikings.mangareader.ui

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import com.vikings.mangareader.R
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.Source
import com.vikings.mangareader.core.SourceManager
import kotlinx.android.synthetic.main.fragment_mangas_list.*

/**
 * A [Fragment] that show all contains of the source.
 * Activities that contain this fragment must implement the
 * [MangasListFragment.Listener] interface
 * to handle interaction events.
 * Use the [MangasListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MangasListFragment: Fragment() {
    companion object {
        const val SOURCE_ID = "MangaListFragment.sourceId"

        @JvmStatic
        fun newInstance(sourceId: Int): MangasListFragment {
            return MangasListFragment().apply {
                arguments = Bundle().apply {
                    putInt(SOURCE_ID, sourceId)
                }
            }
        }
    }

    private lateinit var source: Source

    private val mangasListAdapter = MangasListAdapter()

    private var nextPage = 0//next page to load
    private var hasNextPage = true
    private var loading = false//To not load twice the same page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            source = SourceManager.get(it.getInt(SOURCE_ID))
        }

        activity?.title = source.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mangas_list, container, false)
    }

    override fun onStart() {
        super.onStart()

        mangas_list_refresh.isEnabled = false//No user interaction

        mangas_list_view.apply {
            adapter = mangasListAdapter
            setOnScrollListener(object: AbsListView.OnScrollListener {
                override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {
                    val totalItemsCount = this@apply.count
                    val visibleItemsCount = this@apply.childCount
                    val firstVisibleItem = this@apply.firstVisiblePosition

                    if (!loading && hasNextPage &&
                        totalItemsCount <= firstVisibleItem + visibleItemsCount)
                        loadMangasPage()
                }

                override fun onScrollStateChanged(p0: AbsListView?, p1: Int) { /* Nothing to do*/ }

            })
            setOnItemClickListener { _, _, i, _ ->
                listener?.onMangaSelection(mangasListAdapter.mangas[i])
            }
        }
    }

    private fun loadMangasPage() {
        loading = true
        mangas_list_refresh.isRefreshing = true

        source.fetchLatestMangas(nextPage)
            .subscribe({
                mangas_list_refresh.isRefreshing = false

                mangas_list_view.post {
                    mangasListAdapter.mangas.addAll(it.mangas)
                    mangasListAdapter.notifyDataSetChanged()

                    hasNextPage = it.hasNext
                    if (hasNextPage)
                        ++nextPage//Loading succeeded, so go to next page

                    loading = false
                }
            },{
                mangas_list_refresh.isRefreshing = false

                Snackbar.make(mangas_list_coordinator,
                    R.string.error_mangas_list_load,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry) { _ ->
                        loading = false
                        loadMangasPage()
                    }
                    .show()
            })
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface Listener {
        fun onMangaSelection(manga: Manga)
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
