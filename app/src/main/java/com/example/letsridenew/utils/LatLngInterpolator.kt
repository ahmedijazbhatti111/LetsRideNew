package com.example.letsridenew.utils

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

interface LatLngInterpolator {
    fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng
    class Spherical : LatLngInterpolator {
        /* From github.com/googlemaps/android-maps-utils */
        override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
            // http://en.wikipedia.org/wiki/Slerp
            val fromLat = Math.toRadians(a.latitude)
            val fromLng = Math.toRadians(a.longitude)
            val toLat = Math.toRadians(b.latitude)
            val toLng = Math.toRadians(b.longitude)
            val cosFromLat = cos(fromLat)
            val cosToLat = cos(toLat)
            // Computes Spherical interpolation coefficients.
            val angle = computeAngleBetween(fromLat, fromLng, toLat, toLng)
            val sinAngle = sin(angle)
            if (sinAngle < 1E-6) {
                return a
            }
            val a1 = sin((1 - fraction) * angle) / sinAngle
            val b1 = sin(fraction * angle) / sinAngle
            // Converts from polar to vector and interpolate.
            val x =
                a1 * cosFromLat * cos(fromLng) + b1 * cosToLat * cos(
                    toLng
                )
            val y =
                a1 * cosFromLat * sin(fromLng) + b1 * cosToLat * sin(
                    toLng
                )
            val z = a1 * sin(fromLat) + b1 * sin(toLat)
            // Converts interpolated vector back to polar.
            val lat = atan2(z, sqrt(x * x + y * y))
            val lng = atan2(y, x)
            return LatLng(Math.toDegrees(lat), Math.toDegrees(lng))
        }

        private fun computeAngleBetween(
            fromLat: Double,
            fromLng: Double,
            toLat: Double,
            toLng: Double
        ): Double {
            // Haversine's formula
            val dLat = fromLat - toLat
            val dLng = fromLng - toLng
            return 2 * asin(
                sqrt(
                    sin(dLat / 2).pow(2.0) +
                            cos(fromLat) * cos(toLat) * sin(dLng / 2).pow(2.0)
                )
            )
        }
    }
}