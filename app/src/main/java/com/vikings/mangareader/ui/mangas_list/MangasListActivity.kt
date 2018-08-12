package com.vikings.mangareader.ui.mangas_list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.widget.AbsListView
import com.vikings.mangareader.R
import com.vikings.mangareader.core.Source
import com.vikings.mangareader.core.SourceManager
import com.vikings.mangareader.ui.DrawerActivity
import com.vikings.mangareader.ui.manga.MangaActivity
import kotlinx.android.synthetic.main.activity_mangas_list.*

class MangasListActivity: DrawerActivity() {
    override fun getLayout(): Int = R.layout.activity_mangas_list

    companion object {
        const val SOURCE_ID = "MangaListFragment.sourceId"

        @JvmStatic
        fun getIntent(context: Context, sourceId: Int): Intent {
            val intent = Intent(context, MangasListActivity::class.java)
            intent.putExtra(SOURCE_ID, sourceId)
            return intent
        }
    }

    private lateinit var source: Source

    private val mangasListAdapter = MangasListAdapter()

    private var nextPage = 0//next page to load
    private var hasNextPage = true
    private var loading = false//To not load twice the same page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        source = SourceManager.get(intent.extras?.getInt(SOURCE_ID) ?: -1)
        title = source.name

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
                startActivity(MangaActivity.getIntent(
                    this@MangasListActivity,
                    mangasListAdapter.mangas[i]))
            }
        }
    }

    private fun loadMangasPage() {
        loading = true
        mangas_list_refresh.isRefreshing = true

        Log.i("MangasList", "loading mangas page")

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
}
