package com.example.letsridenew.models

import com.google.android.gms.maps.model.LatLng

class Schedule {
    var source: Location? = null
    var destination: Location? = null
    var pickUpTime: String? = null
    var pickUpDate: String? = null
    private var user: User? = null
    private var routeLatLangs: List<LatLng>? = null

    constructor(
        source: Location?,
        destination: Location?,
        pickUpTime: String?,
        pickUpDate: String?,
        user: User?,
        routeLatLangs: List<LatLng>?
    ) {
        this.source = source
        this.destination = destination
        this.pickUpTime = pickUpTime
        this.pickUpDate = pickUpDate
        this.user = user
        this.routeLatLangs = routeLatLangs
    }

    constructor(
        source: Location?,
        destination: Location?,
        pickUpTime: String?,
        pickUpDate: String?,
        user: User?
    ) {
        this.source = source
        this.destination = destination
        this.pickUpTime = pickUpTime
        this.pickUpDate = pickUpDate
        this.user = user
    }

    constructor() {}

    override fun toString(): String {
        return "Departure Time : $pickUpTime"
    }

    fun toStrings(): String {
        return """
            Departure Time : ${pickUpTime}
            Source : ${source!!.name}
            Destination : ${destination!!.name}
            """.trimIndent()
    }

    fun getUser(): User? {
        return user
    }

    fun setUser(user: User?) {
        this.user = user
    }

    fun getRouteLatLangs(): List<LatLng>? {
        return routeLatLangs
    }

    fun setRouteLatLangs(routeLatLangs: List<LatLng>?) {
        this.routeLatLangs = routeLatLangs
    }
}