package com.example.letsridenew.utils.interfaces

import com.example.letsridenew.models.TrackRecord


interface OnTrackRecordCallback {
    fun onTrackSuccessful(record: TrackRecord?)
    fun onReadTrackRecord(record: TrackRecord?)
}