package com.vikings.mangareader

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.vikings.mangareader.core.SourceManager
import com.vikings.mangareader.source.faker.Faker
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main activity.
 * Display all externals sources.
 *
 * Local source is expected to have direct access to it on
 * a drawer on all principal activities.
 */
class CatalogueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSources()

        //Set source as variable so that if a source is removed between the display
        //and the user click, it launch an error instead of using a wrong source.
        val sources = SourceManager.all()

        source_list.apply {
            adapter = ArrayAdapter(this@CatalogueActivity,
                android.R.layout.simple_spinner_dropdown_item,
                sources.map { it.name })

            onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
                val intent = Intent(this@CatalogueActivity, MangasListActivity::class.java)
                intent.putExtra(MangasListActivity.SOURCE_ID, sources[i].id)
                startActivity(intent)
            }
        }
    }

    private fun initSources() {
        if (SourceManager.all().isNotEmpty())
            return//Avoid reinitializing sources.

        SourceManager.add(Faker())
    }
}
