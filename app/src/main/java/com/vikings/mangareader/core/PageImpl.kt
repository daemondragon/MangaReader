package com.vikings.mangareader.core

import android.graphics.drawable.Drawable

class PageImpl: Page {
    override var sourceId: Int = -1

    override lateinit  var url: String

    override var picture: Drawable? = null
}