package com.example.letsridenew.models

import com.google.android.gms.maps.model.LatLng

class Location {
    var name: String? = null
    var latLng: LatLng? = null
    var time: String? = null

    constructor()

    constructor(name: String?, latLng: LatLng?) {
        this.name = name
        this.latLng = latLng
    }
    constructor(name: String?, latLng: LatLng?, time: String?) {
        this.name = name
        this.latLng = latLng
        this.time = time
    }

}