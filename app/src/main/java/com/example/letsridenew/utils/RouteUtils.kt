package com.example.letsridenew.utils

import com.example.letsridenew.models.Schedule
import com.example.letsridenew.utils.Constants.tolerance
import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat
import kotlin.collections.ArrayList

class RouteUtils {
    companion object{
        private var isSourceLatLang = false
        private var isDestLatLang = false

        fun getEstimatedRouteFromGivenSchedulesList(
            scheduleList: ArrayList<Schedule?>?,
            latLngSource: LatLng,
            latLngDest: LatLng
        ) : ArrayList<Schedule>{
            val routeSchedulers : ArrayList<Schedule> = ArrayList()
            val format = DecimalFormat("0.000")

            println("$latLngDest  check  $latLngSource")
            var t: Double = tolerance
            while (t <= tolerance * 100) {
                for (j in scheduleList!!.indices) {
                    for (k in scheduleList[j]!!.getRouteLatLangs()!!.indices) {
                        println("${scheduleList[j]!!.getRouteLatLangs()!![j].latitude}  !!check!!  ${scheduleList[j]!!.getRouteLatLangs()!![j].longitude}")
                        if (MathUtils.approximatelyEqual(
                                scheduleList[j]!!.getRouteLatLangs()?.get(k)?.latitude!!,
                                latLngSource.latitude,
                                format.format(t).toDouble()
                            ) &&
                            MathUtils.approximatelyEqual(
                                scheduleList[j]!!.getRouteLatLangs()!![k].longitude,
                                latLngSource.longitude,
                                format.format(t).toDouble()
                            )
                        ) {
                            isSourceLatLang = true
                        }

                        if (MathUtils.approximatelyEqual(
                                scheduleList[j]!!.getRouteLatLangs()!![k].latitude,
                                latLngDest.latitude,
                                format.format(t).toDouble()
                            ) &&
                            MathUtils.approximatelyEqual(
                                scheduleList[j]!!.getRouteLatLangs()!![k].longitude,
                                latLngDest.longitude,
                                format.format(t).toDouble()
                            )
                        ) {
                            isDestLatLang = true
                        }
                    }
                    println("$isDestLatLang  11check11  $isSourceLatLang")
                    if (isSourceLatLang && isDestLatLang) {
                        routeSchedulers.add(scheduleList[j]!!)

                        println("${scheduleList[j]!!.getRouteLatLangs()!![j].latitude}  !!check!!  ${scheduleList[j]!!.getRouteLatLangs()!![j].longitude}")
                        isSourceLatLang = false
                        isDestLatLang = false
                    }
                }
                t += tolerance
            }

            return routeSchedulers
        }
    }
}