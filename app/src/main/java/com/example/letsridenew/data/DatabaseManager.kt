package com.example.letsridenew.data

import android.util.Log
import com.example.letsridenew.models.*
import com.example.letsridenew.utils.interfaces.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import java.util.*

class DatabaseManager {

    private val currentUserDb: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val TAG = "DATABASE"
    fun insertRideRequest(request: RideRequest, callback: OnRideRequestCallback) {
        currentUserDb.child("RideRequest")
            .child(request.from_uid.toString())
            .setValue(request)
            .addOnSuccessListener { callback.onRequest(request, "Request has been sent") }
            .addOnFailureListener {
                callback.onRequest(
                    null,
                    "Request did not sent, there is some problem"
                )
            }
    }

    fun insertVehicle(vehicle: Vehicle) {
        currentUserDb.child("Vehicle")
            .child(vehicle.user_id.toString())
            .setValue(vehicle)
    }

    fun insertUser(
        user: User,
        userChangeCallback: OnUserChangeCallback
    ) {
        currentUserDb.child("User")
            .child(user.uid.toString()).setValue(user)
            .addOnSuccessListener {
                userChangeCallback.onSuccessCallback(
                    user.uid,
                    "Changes are updated"
                )
            }
            .addOnFailureListener {
                userChangeCallback.onSuccessCallback(
                    null,
                    "there is some problem"
                )
            }
    }

    fun insertUserSchedule(schedule: Schedule) {
        if (schedule.getUser()?.type == "driver"
        ) currentUserDb.child("Schedule").child("Driver")
            .child(schedule.getUser()!!.uid.toString())
            .setValue(schedule) else currentUserDb.child("Schedule").child("Passenger")
            .child(schedule.getUser()!!.uid.toString())
            .setValue(schedule)
    }

    fun insertTrackRecord(record: TrackRecord) {
        currentUserDb.child("TrackRecords").child(record.id).setValue(record)
    }

    fun insertTrackingLocation(
        location: Location?,
        id: String?
    ) {
        currentUserDb.child("location").child(id!!).setValue(location)
            .addOnSuccessListener { //
                Log.i("tag", "Location update saved")
            }
    }

//    fun readTrackRecord(
//        fromCurUid: String?,
//        trackRecordCallback: OnTrackRecordCallback,
//        childCallback: OnTrackRecordChildCallback
//    ) {
//        currentUserDb.child("TrackRecords").child(fromCurUid!!)
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    val record = TrackRecord(
//                        dataSnapshot.child("fromCurUid").value.toString(),
//                        Location(
//                            dataSnapshot.child("fromCurLoc").child("name").value.toString(),
//                            LatLng(
//                                dataSnapshot.child("fromCurLoc").child("latLng").child("latitude")
//                                    .value.toString().toDouble(),
//                                dataSnapshot.child("fromCurLoc").child("latLng").child("longitude")
//                                    .value.toString().toDouble()
//                            )
//                        ),
//                        Location(
//                            dataSnapshot.child("toCurLoc").child("name").value.toString(),
//                            LatLng(
//                                dataSnapshot.child("toCurLoc").child("latLng").child("latitude")
//                                    .value.toString().toDouble(),
//                                dataSnapshot.child("toCurLoc").child("latLng").child("longitude")
//                                    .value.toString().toDouble()
//                            )
//                        ),
//                        dataSnapshot.child("track_status").value.toString()
//                    )
//                    trackRecordCallback.onTrackSuccessful(record)
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    Log.e("Database", "onCancelled", databaseError.toException())
//                }
//            })
//        currentUserDb.child("TrackRecords").child(fromCurUid)
//            .addChildEventListener(object : ChildEventListener {
//                override fun onChildAdded(
//                    dataSnapshot: DataSnapshot,
//                    s: String?
//                ) {
//                    val record = TrackRecord(
//                        dataSnapshot.child("fromCurUid").value.toString(),
//                        Location(
//                            dataSnapshot.child("fromCurLoc").child("name").value.toString(),
//                            LatLng(
//                                dataSnapshot.child("fromCurLoc").child("latLng").child("latitude")
//                                    .value.toString().toDouble(),
//                                dataSnapshot.child("fromCurLoc").child("latLng").child("longitude")
//                                    .value.toString().toDouble()
//                            )
//                        ),
//                        Location(
//                            dataSnapshot.child("toCurLoc").child("name").value.toString(),
//                            LatLng(
//                                dataSnapshot.child("toCurLoc").child("latLng").child("latitude")
//                                    .value.toString().toDouble(),
//                                dataSnapshot.child("toCurLoc").child("latLng").child("longitude")
//                                    .value.toString().toDouble()
//                            )
//                        ),
//                        dataSnapshot.child("track_status").value.toString()
//                    )
//                    childCallback.onChildAdded(record, s)
//                }
//
//                override fun onChildChanged(
//                    dataSnapshot: DataSnapshot,
//                    s: String?
//                ) {
//                    val record = TrackRecord(
//                        dataSnapshot.child("fromCurUid").value.toString(),
//                        Location(
//                            dataSnapshot.child("fromCurLoc").child("name").value.toString(),
//                            LatLng(
//                                dataSnapshot.child("fromCurLoc").child("latLng").child("latitude")
//                                    .value.toString().toDouble(),
//                                dataSnapshot.child("fromCurLoc").child("latLng").child("longitude")
//                                    .value.toString().toDouble()
//                            )
//                        ),
//                        Location(
//                            dataSnapshot.child("toCurLoc").child("name").value.toString(),
//                            LatLng(
//                                dataSnapshot.child("toCurLoc").child("latLng").child("latitude")
//                                    .value.toString().toDouble(),
//                                dataSnapshot.child("toCurLoc").child("latLng").child("longitude")
//                                    .value.toString().toDouble()
//                            )
//                        ),
//                        dataSnapshot.child("track_status").value.toString()
//                    )
//                    childCallback.onChildChanged(record, s)
//                }
//
//                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
//                override fun onChildMoved(
//                    dataSnapshot: DataSnapshot,
//                    s: String?
//                ) {
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {}
//            })
//    }

    fun readUser(user_id: String?, myUserCallback: MyUserCallback) {
        currentUserDb.child("User").child(user_id!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val currentUser =
                        dataSnapshot.getValue(
                            User::class.java
                        )!!
                    myUserCallback.onCallback(currentUser)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException())
                }
            })
    }

    fun readAllUsers(myUsers: MyUsersListCallback) {
        currentUserDb.child("User").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users =
                    ArrayList<User?>()
                for (ds in dataSnapshot.children) {
                    users.add(ds.getValue(User::class.java))
                }
                myUsers.onUsersRead(users)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })
    }

    fun readAllRideRequests(myRequests: MyRequestsListCallback) {
        val requests = ArrayList<RideRequest?>()
        currentUserDb.child("RideRequest").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    requests.add(ds.getValue(RideRequest::class.java))
                }
                myRequests.onRequestsRead(requests)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })
    }

    fun readRideRequest(
        request: RideRequest,
        onRideRequestCallback: OnRideRequestCallback
    ) {
        currentUserDb.child("RideRequest").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val rideRequest = ds.getValue(RideRequest::class.java)!!
                    if (request.from_uid == rideRequest.from_uid && request.to_uid == rideRequest.to_uid) {
                        onRideRequestCallback.onRequest(
                            rideRequest,
                            "Request successfully read"
                        )
                    } else {
                        onRideRequestCallback.onRequest(null, "Request does not exist")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })
    }

    fun readDriversScheduleFromDb(myScheduleCallback: MyScheduleCallback) {
        val schedulers = ArrayList<Schedule?>()
        val latLngSourceLong: MutableList<String> =
            ArrayList()
        val latLngSourceLat: MutableList<String> =
            ArrayList()
        val latLngSourceName: MutableList<String> =
            ArrayList()
        val latLngDestinationLong: MutableList<String> =
            ArrayList()
        val latLngDestinationLat: MutableList<String> =
            ArrayList()
        val latLngDestinationName: MutableList<String> =
            ArrayList()
        val pickUpTimes: MutableList<String> =
            ArrayList()
        val pickUpDates: MutableList<String> =
            ArrayList()
        val drivers: MutableList<User> =
            ArrayList()
        val routeLatlangs: MutableList<List<LatLng>> =
            ArrayList()
        currentUserDb.child("Schedule").child("Driver")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (uides in dataSnapshot.children) {
                        latLngSourceLong.add(
                            uides.child("source").child("latLng").child("longitude").value
                                .toString()
                        )
                        latLngSourceLat.add(
                            uides.child("source").child("latLng").child("latitude").value
                                .toString()
                        )
                        latLngSourceName.add(
                            uides.child("source").child("name").value.toString()
                        )
                        latLngDestinationLong.add(
                            uides.child("destination").child("latLng").child("longitude").value
                                .toString()
                        )
                        latLngDestinationLat.add(
                            uides.child("destination").child("latLng").child("latitude").value
                                .toString()
                        )
                        latLngDestinationName.add(
                            uides.child("destination").child("name").value.toString()
                        )
                        pickUpTimes.add(uides.child("pickUpTime").value.toString())
                        pickUpDates.add(uides.child("pickUpDate").value.toString())
                        drivers.add(
                            uides.child("user").getValue(
                                User::class.java
                            )!!
                        )
                        val lati: MutableList<String> =
                            ArrayList()
                        val longi: MutableList<String> =
                            ArrayList()
                        val latLngs: MutableList<LatLng> =
                            ArrayList()
                        for (lt in uides.child("routeLatLangs").children) {
                            lati.add(lt.child("latitude").value.toString())
                            longi.add(lt.child("longitude").value.toString())
                        }
                        for (c in longi.indices) {
                            latLngs.add(LatLng(lati[c].toDouble(), longi[c].toDouble()))
                        }
                        routeLatlangs.add(latLngs)
                    }
                    for (i in latLngSourceLat.indices) {
                        val latLngS = LatLng(
                            latLngSourceLat[i].toDouble(),
                            latLngSourceLong[i].toDouble()
                        )
                        val latLngD = LatLng(
                            latLngDestinationLat[i].toDouble(),
                            latLngDestinationLong[i].toDouble()
                        )
                        schedulers.add(
                            Schedule(
                                Location(
                                    latLngSourceName[i],
                                    latLngS
                                ),
                                Location(
                                    latLngDestinationName[i],
                                    latLngD
                                ),
                                pickUpTimes[i],
                                pickUpDates[i],
                                drivers[i],
                                routeLatlangs[i]
                            )
                        )
                    }
                    myScheduleCallback.onCallback(schedulers)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException())
                }
            })
    }

    fun readLocationFromDb(id: String?, callback: OnLocationChangeCallback) {
        currentUserDb.child("location").child(id!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.i("tag", "New location updated:" + dataSnapshot.key)
                    val location =
                        Location(
                            dataSnapshot.child("name").value.toString(),
                            LatLng(
                                dataSnapshot.child("latLng").child("latitude").value.toString()
                                    .toDouble(),
                                dataSnapshot.child("latLng").child("longitude").value
                                    .toString().toDouble()
                            ),
                            dataSnapshot.child("time").value.toString()
                        )
                    callback.onChangeCallback(location)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    fun deleteRideRequestFromDb(request: RideRequest) {
        currentUserDb.child("RideRequest").child(request.from_uid!!).removeValue()
    }

    init {
        currentUserDb.keepSynced(true)
    }
}