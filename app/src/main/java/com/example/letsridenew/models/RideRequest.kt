package com.example.letsridenew.models

import java.io.Serializable

data class RideRequest(var from_uid: String? = null,var to_uid: String? = null, var req_status: String? = null) : Serializable{

}