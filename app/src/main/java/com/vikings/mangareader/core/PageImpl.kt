package com.vikings.mangareader.core

import android.graphics.drawable.Drawable

class PageImpl(override var sourceId: Int): Page {

    override lateinit  var url: String

    override var picture: Drawable? = null
}