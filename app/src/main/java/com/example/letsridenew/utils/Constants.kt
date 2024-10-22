package com.example.letsridenew.utils

import java.text.DecimalFormat

object Constants {
    const val DRIVER = "driver"
    const val PASSENGER = "passenger"
    const val REQUEST_LOCATION_CODE = 99
    const val DEFAULT_ZOOM = 15f
    const val txtMaxLength = 23
    const val routePadding = 250

    const val tolerance = 0.001
    const val timeMargin = 30
    const val PENDING = "Pending"

    const val ACCEPTED = "accepted"
    const val REJECTED = "rejected"
    const val REQUEST = "Request"
    const val TRACKING = "Tracking"

    val format = DecimalFormat("0.000")
}