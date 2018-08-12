package com.vikings.mangareader.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.util.Log
import android.view.MenuItem
import com.vikings.mangareader.R
import com.vikings.mangareader.Singletons
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
class DrawerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        Singletons.initAll(applicationContext)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        initDrawer()
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
            when (menuItem.itemId) {
                R.id.nav_catalogue      -> { TODO("set current fragment to catalogue") }
                R.id.nav_library        -> { TODO("set current fragment to library") }
                R.id.nav_download_queue -> { TODO("set current fragment to dl queue") }
                R.id.nav_settings       -> { TODO("set current fragment to settings") }
            }

            drawer_layout.closeDrawers()

            true
        }

        //drawer_navigation_layout.setCheckedItem(R.id.nav_catalogue)
    }
}
