package com.example.letsridenew.models

class TrackRecord {
    lateinit var id:String
    lateinit var fromCurUid: String
    lateinit var fromCurLoc: Location

    constructor(
        mergeUids: String,
        uid: String,
        location: Location
    )

    constructor()
}