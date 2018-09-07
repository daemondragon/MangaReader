package com.vikings.mangareader.ui.catalogue


import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.vikings.mangareader.R
import com.vikings.mangareader.core.Source
import com.vikings.mangareader.core.SourceManager
import kotlinx.android.synthetic.main.fragment_catalogue.*

class CatalogueFragment : Fragment() {
    companion object {
        fun newInstance(): CatalogueFragment {
            return CatalogueFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SourceManager.observe(this, Observer { list ->
            list?.apply { displaySourceList(this) }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_catalogue, container, false)
    }

    override fun onResume() {
        super.onResume()

        activity?.title = getString(R.string.app_name)
    }

    private fun displaySourceList(sources: List<Source>) {
        Log.i("CatalogueFragment", "displaying source list")

        catalogue_sources_list.apply {
            adapter = ArrayAdapter(this@CatalogueFragment.requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                sources.map { source -> source.name })

            onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
                Log.i("CatalogueFragment", "source ${sources[i].name} selected")
                listener?.onSourceSelected(sources[i].id)
            }
        }
    }

    private var listener: Listener? = null

    interface Listener {
        /**
         * When a source is selected in the source list
         */
        fun onSourceSelected(sourceId: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement CatalogueFragment.Listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}