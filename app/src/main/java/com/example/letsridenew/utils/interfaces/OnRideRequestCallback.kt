package com.example.letsridenew.utils.interfaces

import com.example.letsridenew.models.RideRequest


interface OnRideRequestCallback {
    fun onRequest(request: RideRequest?, message: String?)
}