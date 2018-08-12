package com.vikings.mangareader.ui.catalogue

import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.vikings.mangareader.R
import com.vikings.mangareader.core.SourceManager
import com.vikings.mangareader.ui.DrawerActivity
import com.vikings.mangareader.ui.mangas_list.MangasListActivity
import kotlinx.android.synthetic.main.activity_catalogue.*

class CatalogueActivity : DrawerActivity() {
    override fun getLayout(): Int = R.layout.activity_catalogue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = getString(R.string.app_name)

        val sources = SourceManager.all()
        source_list.apply {
            adapter = ArrayAdapter(this@CatalogueActivity,
                android.R.layout.simple_spinner_dropdown_item,
                sources.map { it.name })

            onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
                Log.i("Catalogue", "source ${sources[i].name} - ${sources[i].id} selected")

                startActivity(MangasListActivity.getIntent(
                    this@CatalogueActivity,
                    sources[i].id)
                )
            }
        }
    }
}
