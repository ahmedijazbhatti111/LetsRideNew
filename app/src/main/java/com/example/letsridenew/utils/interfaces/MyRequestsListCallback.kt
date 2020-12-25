package com.example.letsridenew.utils.interfaces

import com.example.letsridenew.models.RideRequest
import java.util.*

interface MyRequestsListCallback {
    fun onRequestsRead(rideRequests: ArrayList<RideRequest?>?)
}