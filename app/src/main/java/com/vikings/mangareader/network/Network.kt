package com.vikings.mangareader.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class Network constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: Network? = null

        // Must be called directly at the start of the application.
        // Init not done in the getInstance function so that
        // request caller doesn't need to know the context.
        fun init(context: Context) {
            INSTANCE = INSTANCE ?: Network(context)
        }

        fun getInstance(): Network {
            return INSTANCE!!
        }
    }

    private val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}