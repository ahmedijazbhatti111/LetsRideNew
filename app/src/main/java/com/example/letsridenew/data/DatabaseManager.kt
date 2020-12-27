package com.example.letsridenew.data

import android.util.Log
import com.example.letsridenew.models.*
import com.example.letsridenew.utils.interfaces.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import java.lang.Exception
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

    fun insertTrackRecord(record: TrackRecord,onTrackRecordCallback: OnTrackRecordCallback) {
        currentUserDb.child("TrackRecords").child(record.id!!).setValue(record).addOnCompleteListener {
            onTrackRecordCallback.onTrackSuccessful(record)
        }
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

    fun readTrackRecord(
        uid: String?,
        trackRecordCallback: OnTrackRecordCallback
    ) {
        currentUserDb.child("TrackRecords").child(uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var trackSt = ""
                    var driLoc: Location? = null
                    var driId = ""
                    var passLoc: Location? = null
                    var passId = ""
                    var id = ""

                    try { id = dataSnapshot.child("id").value.toString() }catch (e : Exception){}
                    try { passId = dataSnapshot.child("passengerUid").value.toString() }catch (e : Exception){}
                    try { passLoc = Location(
                            dataSnapshot.child("passengerLoc").child("name").value.toString(),
                            LatLng(
                                dataSnapshot.child("passengerLoc").child("latLng").child("latitude").value.toString().toDouble(),
                                dataSnapshot.child("passengerLoc").child("latLng").child("longitude").value.toString().toDouble()
                            )
                        )
                    }catch (e : Exception){}
                    try { driId = dataSnapshot.child("driverUid").value.toString()}catch (e : Exception){}
                    try { driLoc = Location(
                        dataSnapshot.child("driverLoc").child("name").value.toString(),
                        LatLng(
                            dataSnapshot.child("driverLoc").child("latLng").child("latitude").value.toString().toDouble(),
                            dataSnapshot.child("driverLoc").child("latLng").child("longitude").value.toString().toDouble()
                        )
                    )}catch (e : Exception){}
                    try {trackSt = dataSnapshot.child("trackStatus").value.toString()}catch (e : Exception){}

                    val record = TrackRecord()
                    record.id = id
                    record.driverUid = driId
                    record.passengerUid = passId
                    record.trackStatus = trackSt
                    try {
                        record.driverLoc = driLoc
                    }catch (e:Exception){}
                    try {
                        record.passengerLoc = passLoc
                    }catch (e:Exception){}
                    trackRecordCallback.onReadTrackRecord(record)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Database", "onCancelled", databaseError.toException())
                }
            })
    }

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
    fun readPassengerScheduleFromDb(uid:String,myScheduleCallback: SingleScheduleCallback) {
        val schedule = Schedule()
        currentUserDb.child("Schedule").child("Passenger").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(uides: DataSnapshot) {
                    schedule.source!!.latLng =
                        LatLng(
                            uides.child("source").child("latLng").child("longitude").value as Double,
                            uides.child("source").child("latLng").child("latitude").value as Double
                        )
                    schedule.source!!.name = uides.child("source").child("name").value.toString()
                    schedule.destination!!.latLng =
                        LatLng(
                            uides.child("destination").child("latLng").child("longitude").value as Double,
                            uides.child("destination").child("latLng").child("latitude").value as Double
                        )
                    schedule.destination!!.name = uides.child("destination").child("name").value.toString()
                    schedule.pickUpTime = uides.child("pickUpTime").value.toString()
                    schedule.pickUpDate = uides.child("pickUpDate").value.toString()
                    schedule.setUser(uides.child("user").getValue(User::class.java)!!)
                    myScheduleCallback.onCallback(schedule)
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