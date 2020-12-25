package com.example.letsridenew

import android.os.Bundle
import com.example.letsridenew.SplashActivity.Companion.currentUser
import com.example.letsridenew.data.DatabaseManager
import com.example.letsridenew.models.Location
import com.example.letsridenew.models.RideRequest
import com.example.letsridenew.models.TrackRecord
import com.example.letsridenew.utils.Constants
import com.example.letsridenew.utils.TimeUtils
import com.example.letsridenew.utils.Util.Companion.getAddressFromLatLng
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_start_tracking.*
import java.io.IOException

class StartTrackingActivity : MapActivity(){

    private var databaseManager : DatabaseManager? = null
    private var request : RideRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_tracking)

        databaseManager = DatabaseManager()
        request = intent.getSerializableExtra(Constants.REQUEST) as RideRequest

        toolbar2.title = getString(R.string.tracking)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.p_map_route) as SupportMapFragment?
        mapFragment!!.getMapAsync(this@StartTrackingActivity)


        var address = ""
        try {
            address =  applicationContext.getAddressFromLatLng(latLngCurrent!!)
        }catch (e:IOException){

        }


        var mergeUids : String? = ""
        if(currentUser!!.uid==request!!.from_uid){
            mergeUids = "${currentUser!!.uid}__${request!!.to_uid}"
        }else if(currentUser!!.uid==request!!.to_uid){
            mergeUids = "${currentUser!!.uid}__${request!!.from_uid}"
        }
        databaseManager!!.insertTrackRecord(TrackRecord(mergeUids!!,currentUser!!.uid!!, Location(address,latLngCurrent,TimeUtils.getCurrentDateTime())))
    }
}