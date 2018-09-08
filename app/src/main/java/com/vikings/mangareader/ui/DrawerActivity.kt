package com.vikings.mangareader.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.vikings.mangareader.R
import com.vikings.mangareader.core.Chapter
import com.vikings.mangareader.core.Manga
import com.vikings.mangareader.source.Local
import com.vikings.mangareader.ui.catalogue.CatalogueFragment
import com.vikings.mangareader.ui.manga.MangaFragment
import com.vikings.mangareader.ui.mangas_list.MangasListFragment
import com.vikings.mangareader.ui.page.PageActivity
import kotlinx.android.synthetic.main.activity_drawer.*

class DrawerActivity : AppCompatActivity(),
    CatalogueFragment.Listener,
    MangasListFragment.Listener,
    MangaFragment.Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        drawer_navigation_layout.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawer_layout.closeDrawers()

            //Switch to wanted fragment
            when (menuItem.itemId) {
                R.id.nav_catalogue      -> { setFragment(CatalogueFragment.newInstance()); true }
                R.id.nav_library        -> { setFragment(MangasListFragment.newInstance(Local.id)); true }
                R.id.nav_favorites      -> { TODO("set current fragment to favorites") }
                R.id.nav_download_queue -> { TODO("set current fragment to dl queue") }
                R.id.nav_settings       -> { TODO("set current fragment to settings") }
                else                    -> false
            }
        }

        //The home is the catalogue fragment
        setFragment(CatalogueFragment.newInstance())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawers()
                else
                    drawer_layout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSourceSelected(sourceId: Int) {
        setFragmentWithBackStack(MangasListFragment.newInstance(sourceId))
    }

    override fun onMangaSelected(manga: Manga) {
        setFragmentWithBackStack(MangaFragment.newInstance(manga))
    }

    override fun onChapterSelected(chapters: List<Chapter>, position: Int) {
        startActivity(PageActivity.getIntent(this, chapters, position))
    }

    private fun setFragment(fragment: Fragment) {
        for (i in supportFragmentManager.backStackEntryCount downTo 1) {
            supportFragmentManager.popBackStack()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.drawer_fragment, fragment)
            .commit()
    }

    private fun setFragmentWithBackStack(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.drawer_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}
