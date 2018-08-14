package com.vikings.mangareader.ui.manga

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.vikings.mangareader.R
import com.vikings.mangareader.core.Chapter
import com.vikings.mangareader.network.DownloadService
import com.vikings.mangareader.storage.ChapterEntity
import com.vikings.mangareader.ui.page.PageActivity

class ChaptersListAdapter : BaseAdapter() {
    val chapters = mutableListOf<Chapter>()

    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View {
        var result = convertView
        if (result == null) {
            result = LayoutInflater.from(container?.context)
                .inflate(R.layout.chapters_list_item, container, false)
        }

        val chapterName = result?.findViewById<TextView>(R.id.chapters_list_item_name)
        val chapterMenu = result?.findViewById<ImageView>(R.id.chapters_list_item_menu)

        chapterName?.apply {
            text = chapters[position].name
            setOnClickListener { view ->
                view.context.startActivity(PageActivity.getIntent(view.context, chapters, position))
            }
        }

        chapterMenu?.apply {
            setOnClickListener { view ->
                val isLocalStorage = chapters[position] is ChapterEntity
                Log.i("Manga Activity", "Download or remove launched!")

                //Create menu
                val popupMenu = PopupMenu(view.context, view)
                val menuInflater = popupMenu.menuInflater
                menuInflater.inflate(R.menu.chapter_menu, popupMenu.menu)
                popupMenu.menu.findItem(R.id.chapter_download_or_delete) .title =
                    view.context.getString(
                        if (isLocalStorage) { R.string.delete }
                        else { R.string.download })

                //Set menu click
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.chapter_download_or_delete -> {
                            if (isLocalStorage)
                                DownloadService.delete(
                                    view.context,
                                    chapters[position] as ChapterEntity)
                            else
                                DownloadService.download(
                                    view.context,
                                    (view.context as MangaActivity).manga,
                                    chapters[position])

                            true
                        }
                        else -> false
                    }
                }

                popupMenu.show()
            }
        }

        return result!!
    }

    override fun getItem(position: Int): Any {
        return chapters[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return chapters.size
    }
}