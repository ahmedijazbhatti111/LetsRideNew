package com.example.letsridenew.utils.interfaces

import com.example.letsridenew.models.Location


interface OnLocationChangeCallback {
    fun onChangeCallback(location: Location?)
}