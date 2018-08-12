package com.vikings.mangareader

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.Source
import com.vikings.mangareader.core.SourceManager
import kotlinx.android.synthetic.main.activity_mangas_list.*

class MangasListActivity : AppCompatActivity() {
    companion object {
        const val SOURCE_ID = "MangaListActivity.source_id"
    }

    private lateinit var source: Source

    private val mangas = mutableListOf<Manga>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mangas_list)

        source = SourceManager.get(intent.extras?.getInt(SOURCE_ID) ?: -1)

        mangas_list_refresh.isEnabled = false
        loadMangasPage(0)
    }

    private fun loadMangasPage(page: Int) {
        mangas_list_refresh.isRefreshing = true

        source.fetchLatestMangas(page)
            .subscribe({mangasPage ->
                mangas_list_view.apply {
                    mangas.addAll(mangasPage.mangas)

                    adapter = ArrayAdapter(this@MangasListActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        mangas.map { manga -> manga.name })

                    onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
                        TODO("launch manga ${mangas[i].name}")
                    }

                    if (mangasPage.hasNext) {
                        //Add next page loading if list view as reached the bottom
                        setOnScrollListener(object: AbsListView.OnScrollListener{
                            override fun onScroll(view: AbsListView?, firstVisibleItem: Int,
                                                  visibleItemCount: Int, totalItemCount: Int) {
                                if (mangas_list_refresh.isRefreshing)
                                    return
                                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                                    loadMangasPage(page + 1)
                                }
                            }

                            override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
                                /* Do nothing */
                            }
                        })
                    }

                    mangas_list_refresh.isRefreshing = false
                }
            }, {
                mangas_list_refresh.isRefreshing = false

                AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.error_mangas_list_load)
                    .setPositiveButton(R.string.retry) { _, _ ->
                            loadMangasPage(page)
                        }
                    .setNegativeButton(R.string.cancel) { _, _ -> /* Do nothing */ }
                    .show()
            })
    }
}
