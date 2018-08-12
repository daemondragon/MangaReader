package com.vikings.mangareader.ui

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.vikings.mangareader.R
import com.vikings.mangareader.Singletons
import com.vikings.mangareader.core.Manga
import kotlinx.android.synthetic.main.activity_drawer.*

/**
 * Activity that switch the current fragment in function
 * of the selected item in the drawer navigation.
 *
 * Navigation items are:
 * - Catalogue: show all sources
 * - Library: show the local source contents.
 * - Download queue: show all background download.
 * - Settings
 */
class DrawerActivity : AppCompatActivity(),
    CatalogueFragment.Listener,
    MangasListFragment.Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        Singletons.initAll(applicationContext)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        initDrawer()

        //Set starting fragment as the catalogue
        supportFragmentManager
            .beginTransaction()
            .add(R.id.drawer_fragment_layout, CatalogueFragment())
            .commit()
        drawer_navigation_layout.setCheckedItem(R.id.nav_catalogue)
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

    private fun initDrawer() {
        drawer_navigation_layout.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true

            //Switch to wanted fragment
            val fragment = when (menuItem.itemId) {
                R.id.nav_catalogue      -> {
                    CatalogueFragment()
                }
                R.id.nav_library        -> { TODO("set current fragment to library") }
                R.id.nav_download_queue -> { TODO("set current fragment to dl queue") }
                R.id.nav_settings       -> { TODO("set current fragment to settings") }
                else                    -> throw Exception("Unknown menu id")
            }

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.drawer_fragment_layout, fragment)
                .commit()

            drawer_layout.closeDrawers()

            true
        }
    }

    override fun onSourceSelection(sourceId: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.drawer_fragment_layout, MangasListFragment.newInstance(sourceId))
            .addToBackStack(null)
            .commit()
    }

    override fun onMangaSelection(manga: Manga) {
        TODO("not implemented")
    }
}
