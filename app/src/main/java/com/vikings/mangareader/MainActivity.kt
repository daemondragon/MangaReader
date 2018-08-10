package com.vikings.mangareader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.vikings.mangareader.core.SourceManager
import com.vikings.mangareader.source.Mangakakalot

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Init all sources.
        SourceManager.add(Mangakakalot())
    }
}
