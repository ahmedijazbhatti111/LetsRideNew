package com.example.letsridenew.utils

import android.app.AlertDialog
import android.content.Context
import android.location.Geocoder
import android.os.Message
import android.text.TextUtils
import android.widget.Toast
import com.example.letsridenew.R
import com.example.letsridenew.SplashActivity
import com.example.letsridenew.models.Schedule
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.*

class Util {
    companion object{
        var isTrackingComplete = false
        var isRideComplete = false
        var schedule : Schedule? = null
        fun getSafeSubstring(s: String, maxLength: Int): String? {
            if (!TextUtils.isEmpty(s)) {
                if (s.length >= maxLength) {
                    return s.substring(0, maxLength)
                }
            }
            return s
        }
        fun Context.getUrl(
            origin: LatLng,
            dest: LatLng,
            directionMode: String
        ): String? {
            // Origin of route
            val str_origin = "origin=" + origin.latitude + "," + origin.longitude
            // Destination of route
            val str_dest = "destination=" + dest.latitude + "," + dest.longitude
            // Mode
            val mode = "mode=$directionMode"
            // Building the parameters to the web service
            val parameters = "$str_origin&$str_dest&$mode"
            // Output format
            val output = "json"
            // Building the url to the web service
            return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=" + getString(R.string.google_maps_key)
        }

        fun Context.getAddressFromLatLng(latLng: LatLng):String{
            var add : String? = null
            val geocode = Geocoder(this, Locale.getDefault())
            try {
                val addresses = geocode.getFromLocation(latLng.latitude, latLng.longitude, 1)
                try {
                    val obj = addresses[0]
                    add = obj.getAddressLine(0)
                } catch (e: java.lang.Exception) {}
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
            }
            return add!!
        }
        fun Context.showAlertDialog(title:String,message: String,onPositiveBtnClick:OnDialogPositiveClick): AlertDialog? {
            return AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    onPositiveBtnClick.onClick()
                }
                .setNegativeButton(android.R.string.no) { dialogInterface, i -> dialogInterface.cancel() }
                .setIcon(android.R.drawable.ic_dialog_email)
                .show()
        }
    }
    interface OnDialogPositiveClick{
        fun onClick()
    }

}