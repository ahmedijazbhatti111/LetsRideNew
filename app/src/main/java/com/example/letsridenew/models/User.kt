package com.example.letsridenew.models

import java.io.Serializable

class User : Serializable {
    var uid: String? = null
    var name: String? = null
    var email: String? = null
    var phoneNo: String? = null
    var password: String? = null
    var type: String? = null
    var vehicle: Vehicle? = null
    var isLogin = false

    constructor() {}
    constructor(
        uid: String?,
        name: String?,
        email: String?,
        phoneNo: String?,
        password: String?,
        type: String?,
        isLogin: Boolean
    ) {
        this.uid = uid
        this.name = name
        this.email = email
        this.phoneNo = phoneNo
        this.password = password
        this.type = type
        this.isLogin = isLogin
    }

    constructor(
        uid: String?,
        name: String?,
        email: String?,
        phoneNo: String?,
        password: String?,
        type: String?,
        vehicle: Vehicle?,
        isLogin: Boolean
    ) {
        this.uid = uid
        this.name = name
        this.email = email
        this.phoneNo = phoneNo
        this.password = password
        this.type = type
        this.vehicle = vehicle
        this.isLogin = isLogin
    }

}