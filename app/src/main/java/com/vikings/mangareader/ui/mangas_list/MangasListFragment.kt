package com.vikings.mangareader.ui.mangas_list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import com.vikings.mangareader.viewmodels.MangaListViewModel
import com.vikings.mangareader.viewmodels.MangaListViewModelFactory
import kotlinx.android.synthetic.main.fragment_mangas_list.*

class MangasListFragment : Fragment() {
    companion object {
        private const val SOURCE_ID = "MangaListFragment.sourceId"

        fun newInstance(sourceId: Int): MangasListFragment {
            return MangasListFragment().apply {
                arguments = Bundle().apply {
                    putInt(SOURCE_ID, sourceId)
                }
            }
        }
    }

    private lateinit var mangaListViewModel: MangaListViewModel

    private val mangasListAdapter = MangasListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            mangaListViewModel = ViewModelProviders.of(
                this@MangasListFragment,
                MangaListViewModelFactory(this.getInt(SOURCE_ID)))
                .get(MangaListViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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


                    if (totalItemsCount <= firstVisibleItem + visibleItemsCount) {
                        mangaListViewModel.requestMoreMangas()
                    }
                }

                override fun onScrollStateChanged(p0: AbsListView?, p1: Int) { /* Nothing to do*/ }

            })
            setOnItemClickListener { _, _, i, _ ->
                listener?.onMangaSelected(mangasListAdapter.mangas[i])
            }
        }

        mangaListViewModel
            .getMangaList()
            .observe(this, Observer { mangasList ->
                if (mangasList != null)
                    mangasListAdapter.setMangaList(mangasList)
            })

        mangaListViewModel
            .getErrors()
            .observe(this, Observer { error ->
                if (error != null) {
                    Snackbar.make(mangas_list_coordinator,
                        R.string.error_mangas_list_load,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry) { _ ->
                            mangaListViewModel.errorHandled()
                            mangaListViewModel.requestMoreMangas()
                        }
                        .show()
                }
            })

        mangaListViewModel
            .getLoadingState()
            .observe(this, Observer { loadingState -> mangas_list_refresh.isRefreshing = loadingState!! })

        mangaListViewModel.requestMoreMangas()
    }

    override fun onResume() {
        super.onResume()

        activity?.title = mangaListViewModel.getSource().name
    }

    private var listener: Listener? = null

    interface Listener {
        /**
         * When a manga is selected in the manga list
         */
        fun onMangaSelected(manga: Manga)
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
