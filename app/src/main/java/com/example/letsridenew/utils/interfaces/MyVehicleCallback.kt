package com.example.letsridenew.utils.interfaces

import com.example.letsridenew.models.Vehicle


interface MyVehicleCallback {
    fun callback(vehicle: Vehicle?)
}