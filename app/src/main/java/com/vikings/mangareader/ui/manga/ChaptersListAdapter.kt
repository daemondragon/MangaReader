package com.vikings.mangareader.ui.manga

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.vikings.mangareader.R
import com.vikings.mangareader.core.Chapter

class ChaptersListAdapter : BaseAdapter() {
    val chapters = mutableListOf<Chapter>()

    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View {
        var result = convertView
        if (result == null) {
            result = LayoutInflater.from(container?.context)
                .inflate(R.layout.chapters_list_item, container, false)
        }

        result!!.findViewById<TextView>(R.id.chapters_list_item_name)?.text = chapters[position].name
        return result
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