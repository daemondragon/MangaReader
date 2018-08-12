package com.vikings.mangareader.ui.page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ScrollView
import com.vikings.mangareader.R
import com.vikings.mangareader.core.Chapter
import com.vikings.mangareader.core.SourceManager
import kotlinx.android.synthetic.main.activity_page.*
import kotlin.math.abs

class PageActivity : AppCompatActivity() {
    companion object {
        //Ugly but works... (TODO: use parcelable instead)
        private var chapters: List<Chapter>? = null

        const val CHAPTER_INDEX = "PageActivity.chapter_index"

        @JvmStatic
        fun getIntent(context: Context, chapters: List<Chapter>, chapterIndex: Int): Intent {
            val intent = Intent(context, PageActivity::class.java)
            intent.putExtra(CHAPTER_INDEX, chapterIndex)
            PageActivity.chapters = chapters

            return intent
        }
    }

    private lateinit var chapters: List<Chapter>

    private var chapterIndex = 0
    private var pageIndex = 0
    //Used to prevent loading another page when one is already loading (include errors show)
    private var loading = false

    private lateinit var gestureDetector: GestureDetector

    private enum class ChapterSide {//From which side to load the chapter page.
        Start, End
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page)

        setSupportActionBar(page_toolbar)

        // For when the correct solution will have been implemented,
        // not a lot of thing will have to be changed.
        chapters = PageActivity.chapters!!
        chapterIndex = intent.extras?.getInt(CHAPTER_INDEX) ?: 0

        page_refresh.isEnabled = false//No user interaction

        loadChapter(ChapterSide.Start)
        // To detect left and right swipe
        initGestureDetector()
    }

    override fun onDestroy() {
        super.onDestroy()

        chapters[chapterIndex].dispose()
    }

    private fun loadChapter(side: ChapterSide) {
        setTitle()
        page_refresh.isRefreshing = true
        loading = true

        val currentChapter = chapters[chapterIndex]

        Log.i("Page", "loading chapter information")

        SourceManager.get(currentChapter.sourceId)
            .fetchChapterInformation(currentChapter)
            .subscribe({
                    //Load the chapter from the right side if wanted
                    pageIndex = if (side == ChapterSide.Start) 0 else it.pages!!.size - 1

                    //Do not call page_refresh.isRefreshing = false, as the page will load just after.
                    setTitle()
                    loadPage()
                },
                {
                    page_refresh.isRefreshing = false
                    setTitle()

                    Snackbar.make(page_coordinator,
                        R.string.error_chapter_load,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry) { _ ->
                            loading = false
                            loadChapter(side)
                        }
                        .show()
                })
    }

    private fun loadPage() {
        Log.i("Page", "loading page")

        val currentChapter = chapters[chapterIndex]
        if (currentChapter.pages!!.isNotEmpty()) {
            page_refresh.isRefreshing = true
            loading = true

            val currentPage = currentChapter.pages!![pageIndex]
            SourceManager.get(currentChapter.sourceId)
                .fetchPageInformation(currentPage)
                .subscribe({ page ->
                        page_refresh.isRefreshing = false

                        Log.e("Page", page.picture.toString())

                        setTitle()
                        //Go back to the top of the scroll view.
                        page_scroll.fullScroll(ScrollView.FOCUS_UP)
                        manga_page.setImageDrawable(page.picture)

                        loading = false
                    },
                    {
                        page_refresh.isRefreshing = false
                        setTitle()

                        Snackbar.make(page_coordinator,
                            R.string.error_page_load,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.retry) { _ ->
                                loading = false
                                loadPage()
                            }
                            .show()
                    })
        }
        else {//Assume that the current chapter is not null (no bug in the application)
            page_refresh.isRefreshing = false
            setTitle()

            Snackbar.make(page_coordinator,
                R.string.error_empty_chapter,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.go_back) { _ ->
                    loading = false
                    finish()
                }
                .show()
        }
    }

    private fun setTitle() {
        val currentChapter = chapters[chapterIndex]
        title = if (currentChapter.pages == null) {
            chapters[chapterIndex].name
        } else {
            "${pageIndex + 1}/${currentChapter.pages!!.size} - ${currentChapter.name}"
        }
    }

    private fun goToPreviousPage() {
        if (chapterIndex == 0 && pageIndex == 0)
            return

        val currentChapter = chapters[chapterIndex]

        if (pageIndex == 0) {
            currentChapter.dispose()

            --chapterIndex
            loadChapter(ChapterSide.End)
        }
        else {
            currentChapter.pages!![pageIndex].dispose()

            --pageIndex
            loadPage()
        }
    }

    private fun goToNextPage() {
        val currentChapter = chapters[chapterIndex]
        if (chapterIndex + 1 == chapters.size && pageIndex + 1 >= currentChapter.pages?.size ?: 0) {
            finish()//Finish activity as there is nothing more to read.
            return
        }

        if (pageIndex + 1 >= currentChapter.pages?.size ?: 0) {
            currentChapter.dispose()

            ++chapterIndex
            loadChapter(ChapterSide.Start)
        }
        else {
            currentChapter.pages!![pageIndex].dispose()

            ++pageIndex
            loadPage()
        }
    }

    //Give the gesture detector the MotionEvent it need to detect fling
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun initGestureDetector() {
        val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (loading)
                    return true//Already loading, prevent another loading

                val deltaX = e1.x - e2.x
                val deltaY = e1.y - e2.y

                if (abs(deltaX) > abs(deltaY) * 2) {
                    //A swipe occurs
                    if (deltaX < 0)
                        goToPreviousPage()
                    else
                        goToNextPage()
                }

                return true
            }
        }
        gestureDetector = GestureDetector(applicationContext, gestureListener)
    }
}
