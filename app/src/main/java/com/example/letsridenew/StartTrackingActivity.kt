package com.example.letsridenew

import android.os.Bundle
import com.example.letsridenew.SplashActivity.Companion.currentUser
import com.example.letsridenew.data.DatabaseManager
import com.example.letsridenew.models.Location
import com.example.letsridenew.models.RideRequest
import com.example.letsridenew.models.TrackRecord
import com.example.letsridenew.utils.Constants
import com.example.letsridenew.utils.TimeUtils
import com.example.letsridenew.utils.interfaces.OnTrackRecordCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_start_tracking.*

class StartTrackingActivity : MapActivity(), OnTrackRecordCallback {

    private var databaseManager: DatabaseManager? = null
    private var request: RideRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_tracking)

        databaseManager = DatabaseManager()
        request = intent.getSerializableExtra(Constants.REQUEST) as RideRequest

        toolbar2.title = getString(R.string.tracking)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.p_map_route) as SupportMapFragment?
        mapFragment!!.getMapAsync(this@StartTrackingActivity)
    }

    override fun onLocationChanged(location: android.location.Location?) {
        super.onLocationChanged(location)
        latLngCurrent?.let { setLocationOnMap(it) }
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
        databaseManager!!.readTrackRecord(record!!.id,this)
    }

    override fun onReadTrackRecord(record: TrackRecord?) {

    }
}