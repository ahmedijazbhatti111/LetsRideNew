package com.example.letsridenew.models

import java.io.Serializable

class RideRequest : Serializable{
    var from_uid: String? = null
    var to_uid: String? = null
    var req_status: String? = null

    constructor()

    constructor(uid: String?, uid1: String?, pending: String)
}