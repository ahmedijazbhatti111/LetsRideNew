package com.example.letsridenew

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.letsridenew.SplashActivity.Companion.currentUser
import com.example.letsridenew.data.DatabaseManager
import com.example.letsridenew.directionhelpers.FetchURL
import com.example.letsridenew.directionhelpers.TaskLoadedCallback
import com.example.letsridenew.models.RideRequest
import com.example.letsridenew.utils.ConnectionHelper
import com.example.letsridenew.utils.Constants
import com.example.letsridenew.utils.NotificationHelper
import com.example.letsridenew.utils.Util.Companion.getUrl
import com.example.letsridenew.utils.Util.Companion.schedule
import com.example.letsridenew.utils.interfaces.OnRideRequestCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_driver_info.*

class DriverInfoActivity : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback, TaskLoadedCallback,
    OnRideRequestCallback {
    private lateinit var progressDialog: ProgressDialog
    private lateinit var notificationTile: String
    private lateinit var notificationMessage: String
    private lateinit var topic: String
    private lateinit var currentPolyline: Polyline

    private var sydney1: LatLng = LatLng(-8.579892, 116.095239)
    private var sydney2 = LatLng(-8.579892, 116.095239)

    private var mMap : GoogleMap? = null

    private var marker1 : Marker? = null
    private var marker2 : Marker? = null

    private val databaseManager = DatabaseManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_info)

        progressDialog = ProgressDialog(this)

        toolbar2.title = getString(R.string.driver_route)
        toolbar2.navigationIcon = ResourcesCompat.getDrawable(resources,R.drawable.icon_back,theme)
        toolbar2.setNavigationOnClickListener { onBackPressed() }

        sydney2 = schedule!!.destination!!.latLng!!
        sydney1 = schedule!!.source!!.latLng!!
        val mapFragment = supportFragmentManager.findFragmentById(R.id.p_map_route) as SupportMapFragment?
        mapFragment!!.getMapAsync(this@DriverInfoActivity)

        driverDestination.text = schedule!!.destination!!.name
        driverSource.text = schedule!!.source!!.name
        driverDepTime.text = schedule!!.pickUpTime
        vName.text = schedule!!.getUser()!!.vehicle!!.name
        vNumber.text = schedule!!.getUser()!!.vehicle!!.number
        vColor.text = schedule!!.getUser()!!.vehicle!!.color
        vType.text = schedule!!.getUser()!!.vehicle!!.type
        vAvaSeats.text = schedule!!.getUser()!!.vehicle!!.vacantSeats

        if(currentUser!!.type == Constants.DRIVER){
            btnSentReq.visibility = View.GONE
        }else {
            btnSentReq.visibility = View.VISIBLE
        }

        btnSentReq.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSentReq -> {
                AlertDialog.Builder(this@DriverInfoActivity)
                        .setTitle("Request Driver")
                        .setMessage("Are you sure you want to ride with this Driver?")
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            Toast.makeText(this@DriverInfoActivity, "Requesting to Driver", Toast.LENGTH_SHORT).show()
                            notificationTile = schedule!!.source!!.name + "-" + schedule!!.destination!!.name
                            notificationMessage = currentUser!!.name + " send you riding request for pickup at :" + schedule!!.pickUpTime
                            topic = schedule!!.getUser()!!.uid!! //topic must match with what the receiver subscribed to
                            requestToDriver()
                        }
                        .setNegativeButton(android.R.string.no) { dialogInterface, i -> dialogInterface.cancel() }
                        .setIcon(android.R.drawable.ic_dialog_email)
                        .show()
            }
        }
    }

    private fun requestToDriver() {
        progressDialog.setTitle("Requesting to Driver");
        progressDialog.setMessage("Please wait for response.. ");
        progressDialog.setCancelable(false)
        progressDialog.show()

        val request = RideRequest(currentUser!!.uid, schedule!!.getUser()!!.uid,Constants.PENDING)
        databaseManager.insertRideRequest(request,this)
    }

    override fun onMapReady(googleMap : GoogleMap ) {
        mMap = googleMap
        if(marker1 != null && marker2 != null){
            marker1!!.remove()
            marker2!!.remove()
        }
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney1, Constants.DEFAULT_ZOOM))
        marker1 = mMap!!.addMarker(MarkerOptions()
                .position(sydney1)
                .title("Source")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney2, Constants.DEFAULT_ZOOM))
        marker2 = mMap!!.addMarker(MarkerOptions()
                .position(sydney2)
                .title("Destination")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
        if(ConnectionHelper.haveNetworkConnection(this)) {
            FetchURL(this@DriverInfoActivity).execute(getUrl(sydney1, sydney2, "driving"), "driving")
            val builder = LatLngBounds.builder().include(sydney1).include(sydney2).build()
            try{
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(builder, 150))
            }catch (e : Exception){ }
        } else {
            Toast.makeText(
                this,
                "Route dose not show because of connection error",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onTaskDone(vararg values: Any?) {
        currentPolyline = mMap!!.addPolyline(values[0] as PolylineOptions?)
    }

    var isRequestAccepted = false
    var isRequestSent = false

    override fun onRequest(request: RideRequest?, message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        if(request!=null) {
            when (request.req_status) {
                Constants.PENDING -> {
                    if(!isRequestSent) {
                        NotificationHelper(notificationTile, notificationMessage, topic).sendNotification(this@DriverInfoActivity)
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        isRequestSent = true
                        databaseManager.readRideRequest(request, this)
                    }
                }
                Constants.ACCEPTED -> {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Request Accepted", Toast.LENGTH_LONG).show()
                    finish()
                    startActivity(Intent(this,StartTrackingActivity::class.java).putExtra(Constants.REQUEST,request))
                }
                Constants.REJECTED -> {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Request Rejected", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }else{
            progressDialog.dismiss()
            Toast.makeText(this,message, Toast.LENGTH_LONG).show()
        }
    }
}