package com.vikings.mangareader.ui.manga

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.vikings.mangareader.R
import com.vikings.mangareader.core.Chapter
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.ui.BaseActivity
import com.vikings.mangareader.ui.page.PageActivity


class MangaActivity : BaseActivity(), MangaFragment.Listener {
    override fun getLayout(): Int = R.layout.activity_manga

    companion object {
        const val MANGA = "MangaActivity.manga"

        @JvmStatic
        fun getIntent(context: Context, manga: Manga): Intent {
            val intent = Intent(context, MangaActivity::class.java)
            intent.putExtra(MANGA, manga)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manga = intent.extras?.getSerializable(MANGA) as Manga

        title = manga.name

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.manga_fragment, MangaFragment.newInstance(manga))
            .commit()

    }

    override fun onChapterSelected(chapters: List<Chapter>, position: Int) {
        startActivity(PageActivity.getIntent(this, chapters, position))
    }


}
