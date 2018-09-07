package com.vikings.mangareader.ui.catalogue

import android.util.Log
import com.vikings.mangareader.R
import com.vikings.mangareader.ui.BaseActivity
import com.vikings.mangareader.ui.mangas_list.MangasListActivity

class CatalogueActivity : BaseActivity(), CatalogueFragment.Listener {
    override fun getLayout(): Int = R.layout.activity_catalogue

    override fun onResume() {
        super.onResume()

        title = getString(R.string.app_name)
    }

    override fun onSourceSelected(sourceId: Int) {
        Log.i("Catalogue", "source $sourceId selected")

        startActivity(MangasListActivity.getIntent(this@CatalogueActivity, sourceId))
    }
}
