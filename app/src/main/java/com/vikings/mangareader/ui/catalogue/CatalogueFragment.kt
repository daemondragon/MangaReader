package com.vikings.mangareader.ui.catalogue

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.vikings.mangareader.R
import com.vikings.mangareader.core.SourceManager
import kotlinx.android.synthetic.main.fragment_catalogue.*

/**
 * A simple [Fragment] subclass that shows all sources.
 * Activities that contain this fragment must implement the
 * [CatalogueFragment.OnSourceSelection] interface
 * to handle interaction events.
 */
class CatalogueFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_catalogue, container, false)
    }

    override fun onStart() {
        super.onStart()

        val sources = SourceManager.all()
        source_list.apply {
            adapter = ArrayAdapter(this@CatalogueFragment.requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                sources.map { it.name })

            onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
                Log.i("Catalogue", "source ${sources[i].name} - ${sources[i].id} selected")
                listener?.onSourceSelection(sources[i].id)
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnSourceSelection {
        fun onSourceSelection(sourceId: Int)
    }

    private var listener: OnSourceSelection? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSourceSelection) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnSourceSelection")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}