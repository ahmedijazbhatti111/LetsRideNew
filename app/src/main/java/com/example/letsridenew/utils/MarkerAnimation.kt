package com.example.letsridenew.utils

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

object MarkerAnimation {
    fun animateMarkerToGB(
        marker: Marker,
        finalPosition: LatLng?,
        latLngInterpolator: LatLngInterpolator
    ) {
        val startPosition: LatLng = marker.position
        val handler = Handler(Looper.myLooper()!!)
        val start = SystemClock.uptimeMillis()
        val interpolator: Interpolator = AccelerateDecelerateInterpolator()
        val durationInMs = 2000f
        handler.post(object : Runnable {
            var elapsed: Long = 0
            var t = 0f
            var v = 0f
            override fun run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start
                t = elapsed / durationInMs
                v = interpolator.getInterpolation(t)
                marker.position = latLngInterpolator.interpolate(v, startPosition, finalPosition!!)
                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                }
            }
        })
    }
}