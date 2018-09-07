package com.vikings.mangareader.ui.mangas_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.vikings.mangareader.R
import com.vikings.mangareader.core.Manga

class MangasListAdapter : BaseAdapter() {
    val mangas = mutableListOf<Manga>()

    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View {
        var result = convertView
        if (result == null) {
            result = LayoutInflater.from(container?.context)
                .inflate(R.layout.item_mangas_list, container, false)
        }

        result!!.findViewById<TextView>(R.id.mangas_list_item_name)?.text = mangas[position].name
        return result
    }

    override fun getItem(position: Int): Any {
        return mangas[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mangas.size
    }
}