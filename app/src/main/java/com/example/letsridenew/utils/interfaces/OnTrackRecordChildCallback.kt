package com.example.letsridenew.utils.interfaces

import com.example.letsridenew.models.TrackRecord


interface OnTrackRecordChildCallback {
    fun onChildAdded(record: TrackRecord?, s: String?)
    fun onChildChanged(record: TrackRecord?, s: String?)
}