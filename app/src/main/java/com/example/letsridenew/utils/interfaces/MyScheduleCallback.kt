package com.example.letsridenew.utils.interfaces

import com.example.letsridenew.models.Schedule
import java.util.*

interface MyScheduleCallback {
    fun onCallback(schedules: ArrayList<Schedule?>?)
}