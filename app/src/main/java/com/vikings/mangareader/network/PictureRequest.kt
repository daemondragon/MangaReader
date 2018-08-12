package com.vikings.mangareader.network

import android.graphics.drawable.Drawable
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser

class PictureRequest(
    url: String,
    private val listener: Response.Listener<Drawable>,
    errorListener: Response.ErrorListener
) : Request<Drawable>(Method.GET, url, errorListener) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<Drawable> {
        return try {
            Response.success(
                Drawable.createFromStream((response.data ?: ByteArray(0)).inputStream(),
                    "picture"),
                HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            Response.error(VolleyError(e))
        }
    }

    override fun deliverResponse(response: Drawable?) {
        listener.onResponse(response)
    }
}