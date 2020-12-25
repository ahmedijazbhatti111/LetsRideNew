package com.example.letsridenew.models

class TrackRecord(
    var id: String? = null,
    var passengerUid: String? = null,
    var passengerLoc: Location? = null,
    var driverUid: String? = null,
    var driverLoc: Location? = null,
    var trackStatus: String? = null
)