package com.example.letsridenew

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.Listener
import com.example.letsridenew.utils.Constants
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderApi
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

open class MapActivity : AppCompatActivity() ,
    Listener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    OnMapReadyCallback,
    LocationListener {

    private var locationRequest: LocationRequest? = null
    private var client: GoogleApiClient? = null
    private var lastLocation: Location? = null
    private var currentLocationMarker: Marker? = null
    protected var latLngCurrent: LatLng? = null
    protected var mMap: GoogleMap? = null
    private var mFusedLocationProviderClient: FusedLocationProviderApi? = null

    private var easyWayLocation: EasyWayLocation? = null

    private val tag = "LocationActivity"

    private var isFirstTimeCurrentLocation = true

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        mFusedLocationProviderClient = LocationServices.FusedLocationApi
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EasyWayLocation.LOCATION_SETTING_REQUEST_CODE -> easyWayLocation!!.onActivityResult(resultCode)
        }
    }

    override fun onConnected(p0: Bundle?) {
        locationRequest = LocationRequest()
        locationRequest!!.interval = 1000
        locationRequest!!.fastestInterval = 1000
        locationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.i(tag, "Connection Suspended")
        client!!.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i(tag, "Connection failed. Error: " + connectionResult.errorCode)
    }

    override fun onLocationChanged(location: Location?) {
        lastLocation = location
        currentLocationMarker?.remove()

        latLngCurrent = LatLng(location!!.latitude, location.longitude)

        if (isFirstTimeCurrentLocation) {
            Log.d(tag, "onClick: clicked gps icon:::$latLngCurrent")
            try {
                mMap!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLngCurrent,
                        Constants.DEFAULT_ZOOM
                    )
                )
            } catch (ignored: Exception) {

            }
            isFirstTimeCurrentLocation = false
        }
    }

    @Synchronized
    protected open fun buildGoogleApiClient() {
        client = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        client!!.connect()
    }

    override fun locationCancelled() {
        Toast.makeText(this, "Location Cancelled", Toast.LENGTH_SHORT).show()
        easyWayLocation = EasyWayLocation(this, false, this)
    }

    override fun locationOn() {
        Toast.makeText(this, "Location ON", Toast.LENGTH_SHORT).show()

    }

    override fun currentLocation(location: Location?) {
        val data = StringBuilder()
        data.append(location!!.latitude)
        data.append(" , ")
        data.append(location.longitude)

        latLngCurrent = LatLng(location.latitude, location.longitude)

        if (isFirstTimeCurrentLocation) {
            Log.d(tag, "onClick: clicked gps icon:::$latLngCurrent")
            try {
                mMap!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLngCurrent,
                        Constants.DEFAULT_ZOOM
                    )
                )
            } catch (ignored: Exception) {

            }
            isFirstTimeCurrentLocation = false
        }

        Toast.makeText(this, "Location: $latLngCurrent", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        easyWayLocation = EasyWayLocation(this, true, this)
        easyWayLocation!!.startLocation()
    }

    override fun onPause() {
        super.onPause()
        easyWayLocation!!.endUpdates()
        if (client != null) {
            mFusedLocationProviderClient?.removeLocationUpdates(client, this)
        }
    }

    open fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK") { _, _ -> //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@MapActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            Constants.REQUEST_LOCATION_CODE
                        )
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Constants.REQUEST_LOCATION_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_LOCATION_CODE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (client == null) {
                            buildGoogleApiClient()
                        }
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        //Initialize Google Play Services

        //mMap!!.isMyLocationEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled = true
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        } else {
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true
        }
    }
}