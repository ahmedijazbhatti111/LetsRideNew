package com.example.letsridenew.utils

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class NotificationRequestSingleton private constructor(private val ctx: Context) {
    private var requestQueue: RequestQueue?
    private fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.applicationContext)
        }
        return requestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>?) {
        getRequestQueue()!!.add(req)
    }

    companion object {
        private var instance: NotificationRequestSingleton? = null

        @Synchronized
        fun getInstance(context: Context): NotificationRequestSingleton? {
            if (instance == null) {
                instance =
                    NotificationRequestSingleton(context)
            }
            return instance
        }
    }

    init {
        requestQueue = getRequestQueue()
    }
}