package com.vikings.mangareader.ui.mangas_list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.vikings.mangareader.R
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.core.SourceManager
import com.vikings.mangareader.ui.DrawerActivity
import com.vikings.mangareader.ui.manga.MangaActivity

class MangasListActivity: DrawerActivity(), MangasListFragment.Listener {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sourceId = intent.extras?.getInt(SOURCE_ID) ?: -1

        title = SourceManager.get(sourceId).name

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.manga_list_fragment, MangasListFragment.newInstance(sourceId))
            .commit()
    }

    override fun onMangaSelected(manga: Manga) {
        startActivity(MangaActivity.getIntent(this, manga))
    }
}
