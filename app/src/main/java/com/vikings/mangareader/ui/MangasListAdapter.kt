package com.vikings.mangareader.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.vikings.mangareader.R
import com.vikings.mangareader.core.Manga

class MangasListAdapter
    : RecyclerView.Adapter<MangasListAdapter.ViewHolder>() {

    val mangas = mutableListOf<Manga>()

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.mangas_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mangas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.mangas_list_item_name)?.text = mangas[position].name
    }
}