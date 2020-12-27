package com.example.letsridenew

import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.easywaylocation.draw_path.DirectionUtil
import com.example.easywaylocation.draw_path.PolyLineDataBean
import com.example.letsridenew.SplashActivity.Companion.currentUser
import com.example.letsridenew.data.DatabaseManager
import com.example.letsridenew.models.Location
import com.example.letsridenew.models.RideRequest
import com.example.letsridenew.models.Schedule
import com.example.letsridenew.models.TrackRecord
import com.example.letsridenew.utils.*
import com.example.letsridenew.utils.Util.Companion.showAlertDialog
import com.example.letsridenew.utils.interfaces.OnTrackRecordCallback
import com.example.letsridenew.utils.interfaces.SingleScheduleCallback
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_start_tracking.*
import java.lang.Exception

class StartTrackingActivity : MapActivity(), OnTrackRecordCallback {

    private val markerOptions = MarkerOptions()
    private var directionUtil: DirectionUtil? = null
    private var databaseManager: DatabaseManager? = null
    private var request: RideRequest? = null
    private var locationCounter = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_tracking)

        databaseManager = DatabaseManager()
        request = intent.getSerializableExtra(Constants.REQUEST) as RideRequest

        toolbar2.title = getString(R.string.tracking)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.p_map_route) as SupportMapFragment?
        mapFragment!!.getMapAsync(this@StartTrackingActivity)
        cancelBtn.setOnClickListener {
            applicationContext.showAlertDialog(
                "Cancel Ride",
                "Are you sure you want to cancel this ride",
                object : Util.OnDialogPositiveClick {
                    override fun onClick() {
                        Util.isTrackingComplete = true
                        finish()
                    }
                })
        }
        startRideBtn.setOnClickListener {
            showAlertDialog("Start Ride","Do you want to start this Ride",object : Util.OnDialogPositiveClick{
                override fun onClick() {
                    Util.isTrackingComplete = true
                    completeRideBtn.visibility = View.VISIBLE
                    startRideBtn.visibility = View.GONE
                }
            })
        }
        completeRideBtn.setOnClickListener {
            showAlertDialog("Complete Ride","Do you want to complete this ride",object : Util.OnDialogPositiveClick{
                override fun onClick() {
                    completeRideBtn.visibility = View.GONE
                }
            })
        }
    }

    override fun onLocationChanged(location: android.location.Location?) {
        super.onLocationChanged(location)

        if(!Util.isTrackingComplete && !Util.isRideComplete) {
            latLngCurrent?.let { setLocationOnMap(it) }
        }else if(!Util.isRideComplete){
            databaseManager!!.readPassengerScheduleFromDb(request!!.from_uid!!,object : SingleScheduleCallback,
                DirectionUtil.DirectionCallBack {
                override fun onCallback(schedule: Schedule) {
                    markerOptions.position(schedule.destination!!.latLng!!)
                    markerOptions.title(Util.getSafeSubstring(record!!.driverLoc!!.name!!, Constants.txtMaxLength) + "...")
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_b))
                    mMap!!.addMarker(markerOptions)
                    val wayPoints = ArrayList<LatLng>()
                    wayPoints.add(latLngCurrent!!)
                    wayPoints.add(schedule.destination!!.latLng!!)
                    directionUtil = DirectionUtil.Builder()
                        .setDirectionKey(getString(R.string.google_maps_key))
                        .setOrigin(latLngCurrent!!)
                        .setWayPoints(wayPoints)
                        .setGoogleMap(mMap!!)
                        .setPolyLineWidth(8)
                        .setCallback(this)
                        .setPathAnimation(false)
                        .setPolyLinePrimaryColor(resources.getColor(R.color.black))
                        .setDestination(schedule.destination!!.latLng!!)
                        .build()
                    directionUtil!!.drawPath()
                }

                override fun pathFindFinish(polyLineDetails: HashMap<String, PolyLineDataBean>) {

                }
            })

        }
    }

    private fun setLocationOnMap(location: LatLng) {
        val address = ""
//        try {
//            address =  applicationContext.getAddressFromLatLng(latLngCurrent!!)
//        }catch (e:IOException){
//
//        }
        var latLngDriver: Location? = null
        var latLngPassenger: Location? = null
        var uidDriver: String? = null
        var uidPassenger: String? = null
        var mergeUids: String? = ""

        if (currentUser!!.type == Constants.DRIVER) {
            latLngDriver = Location(address, location, TimeUtils.getCurrentDateTime())
            uidDriver = currentUser!!.uid
            mergeUids = "${request!!.from_uid}__${request!!.to_uid}"
        } else if (currentUser!!.type == Constants.PASSENGER) {
            latLngPassenger = Location(address, location, TimeUtils.getCurrentDateTime())
            uidPassenger = currentUser!!.uid
            mergeUids = "${request!!.from_uid}__${request!!.to_uid}"
        }

        databaseManager!!
            .insertTrackRecord(
                TrackRecord(
                    mergeUids!!,
                    uidPassenger,
                    latLngPassenger,
                    uidDriver,
                    latLngDriver,
                    Constants.TRACKING
                ),
                this
            )
    }

    override fun onTrackSuccessful(record: TrackRecord?) {
        databaseManager!!.readTrackRecord(record!!.id, object : OnTrackRecordCallback,
            DirectionUtil.DirectionCallBack {
            override fun onTrackSuccessful(record: TrackRecord?) {}

            override fun onReadTrackRecord(record: TrackRecord?) {

                if (locationCounter == 10) {
                    //mMap!!.clear()
                    var latLng: LatLng? = null

                    if (currentUser!!.type == Constants.DRIVER) {
                        try {
                            latLng = record?.passengerLoc!!.latLng!!
                            markerOptions2.position(latLng)
                            markerOptions2.title(
                                Util.getSafeSubstring(
                                    record.passengerLoc!!.name!!,
                                    Constants.txtMaxLength
                                ) + "..."
                            )
                            markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_male_passenger_waiting))
                        } catch (e: Exception) {
                        }

                    } else if (currentUser!!.type == Constants.PASSENGER) {
                        try {
                            latLng = record?.driverLoc!!.latLng!!
                            markerOptions2.position(latLng)
                            markerOptions2.title(
                                Util.getSafeSubstring(
                                    record.driverLoc!!.name!!,
                                    Constants.txtMaxLength
                                ) + "..."
                            )
                            markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.sport_car))
                        } catch (e: Exception) {
                        }
                    }
                    try {
                        mMap!!.addMarker(markerOptions2)
                        val wayPoints = ArrayList<LatLng>()
                        wayPoints.add(latLngCurrent!!)
                        wayPoints.add(latLng!!)
                        directionUtil = DirectionUtil.Builder()
                            .setDirectionKey(getString(R.string.google_maps_key))
                            .setOrigin(latLngCurrent!!)
                            .setWayPoints(wayPoints)
                            .setGoogleMap(mMap!!)
                            .setPolyLineWidth(8)
                            .setCallback(this)
                            .setPathAnimation(false)
                            .setPolyLinePrimaryColor(resources.getColor(R.color.black))
                            .setDestination(latLng)
                            .build()
                        directionUtil!!.drawPath()
                    } catch (e: Exception) {
                    }
                    try {
                        //if(record!!.driverLoc!!.latLng != null) {
                        locationCounter = 0
                        //}
                        if (MathUtils.approximatelyEqual(
                                latLngCurrent!!.latitude,
                                latLng!!.latitude,
                                Constants.format.format(Constants.tolerance).toDouble()
                            ) &&
                            MathUtils.approximatelyEqual(
                                latLngCurrent!!.longitude,
                                latLng.longitude,
                                Constants.format.format(Constants.tolerance).toDouble()
                            )
                        ) {
                            if (currentUser!!.type == Constants.DRIVER) {
                                NotificationHelper(
                                    getString(R.string.trac_noti_title),
                                    getString(R.string.track_noti_message),
                                    record!!.passengerUid.toString()
                                ).sendNotification(this@StartTrackingActivity)
                                startRideBtn.visibility = View.VISIBLE
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
                locationCounter++


            }

            override fun pathFindFinish(polyLineDetails: HashMap<String, PolyLineDataBean>) {
                for (i in polyLineDetails.keys) {
                    polyLineDetails[i]?.time?.let { Log.v("sample1111", it) }
                }
            }
        })
    }

    override fun onReadTrackRecord(record: TrackRecord?) {
        this.record = record
    }

    private var record: TrackRecord? = null
    private val markerOptions2 = MarkerOptions()

    override fun onMapReady(googleMap: GoogleMap?) {
        super.onMapReady(googleMap)
    }
}