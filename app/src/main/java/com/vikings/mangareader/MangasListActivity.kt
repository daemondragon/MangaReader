package com.vikings.mangareader

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.vikings.mangareader.core.Source
import com.vikings.mangareader.core.SourceManager
import kotlinx.android.synthetic.main.activity_mangas_list.*

class MangasListActivity : AppCompatActivity() {
    companion object {
        const val SOURCE_ID = "MangaListActivity.source_id"
    }

    private lateinit var source: Source

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mangas_list)

        source = SourceManager.get(intent.extras?.getInt(SOURCE_ID) ?: -1)

        source.fetchLatestMangas(0)
            .subscribe({mangasPage ->
                mangas_list_view.apply {
                    adapter = ArrayAdapter(this@MangasListActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        mangasPage.mangas.map { manga -> manga.name })

                    onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
                        TODO("launch manga ${mangasPage.mangas[i].name}")
                    }

                    //TODO: add next page and previous page loading ?
                    //use a recycler view ?
                }
            }, {
                TODO("add error handling")
            })
    }
}
