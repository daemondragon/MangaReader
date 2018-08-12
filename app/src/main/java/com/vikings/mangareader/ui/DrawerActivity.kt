package com.vikings.mangareader.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.vikings.mangareader.R
import com.vikings.mangareader.Singletons
import kotlinx.android.synthetic.main.activity_drawer.*

/**
 * Abstract activity whose only purpose is to handle
 * all activity that need a Drawer
 */
abstract class DrawerActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        Singletons.initAll(applicationContext)

        //Add child layout
        layoutInflater.inflate(getLayout(), drawer_content_layout)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        drawer_navigation_layout.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true

            //Switch to wanted fragment
            val intent = when (menuItem.itemId) {
                R.id.nav_catalogue      -> { Intent(applicationContext, CatalogueActivity::class.java) }
                R.id.nav_library        -> { TODO("set current fragment to library") }
                R.id.nav_download_queue -> { TODO("set current fragment to dl queue") }
                R.id.nav_settings       -> { TODO("set current fragment to settings") }
                else                    -> throw Exception("Unknown menu id")
            }

            drawer_layout.closeDrawers()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            true
        }
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

    /**
     * Get the layout wanted that will be placed as a child
     * of the drawer layout.
     */
    abstract fun getLayout(): Int
}
