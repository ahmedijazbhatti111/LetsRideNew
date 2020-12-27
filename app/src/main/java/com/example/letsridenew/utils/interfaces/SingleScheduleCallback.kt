package com.example.letsridenew.utils.interfaces

import com.example.letsridenew.models.Schedule

interface SingleScheduleCallback {
    fun onCallback(schedule:Schedule)
}