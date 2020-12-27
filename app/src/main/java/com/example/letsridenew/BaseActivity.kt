@file:Suppress("DEPRECATION")

package com.example.letsridenew

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.letsridenew.SplashActivity.Companion.currentUser
import com.example.letsridenew.data.AuthenticationManager
import com.example.letsridenew.data.DatabaseManager
import com.example.letsridenew.data.PushNotificationManager
import com.example.letsridenew.utils.directionhelpers.FetchURL
import com.example.letsridenew.utils.directionhelpers.GetDirectionsData
import com.example.letsridenew.utils.directionhelpers.TaskLoadedCallback
import com.example.letsridenew.utils.ConnectionHelper
import com.example.letsridenew.utils.Constants
import com.example.letsridenew.utils.Util
import com.example.letsridenew.utils.Util.Companion.getUrl
import com.example.letsridenew.utils.interfaces.DiractionCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.compat.AutocompleteFilter
import com.google.android.libraries.places.compat.Place
import com.google.android.libraries.places.compat.ui.PlaceAutocompleteFragment
import com.google.android.libraries.places.compat.ui.PlaceSelectionListener
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.content_base.*
import java.io.IOException
import java.util.*
import kotlin.system.exitProcess

open class BaseActivity : MapActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    TaskLoadedCallback,
    View.OnClickListener,
    PlaceSelectionListener,
    DiractionCallback,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraIdleListener {

    private var add: String? = null
    private val tag = "MapsActivate"

    private var mapFragment: SupportMapFragment? = null


    private var progressBar: ProgressBar? = null
    private lateinit var sourceSearcher: PlaceAutocompleteFragment
    protected var databaseManager: DatabaseManager? = null
    private lateinit var destSearcher: PlaceAutocompleteFragment

    private var mYear = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0

    protected var latLngDest: LatLng? = null
    protected var latLngSource: LatLng? = null

    var sourceName: String? = null
    var destinationName: String? = null

    private var sourcePlace: Place? = null
    private var destinationPlace: Place? = null
    protected var currentPolyline: Polyline? = null



    private var flag = true
    private var flagSource = true
    private var isSourceConfirm = false
    private var isRoutePicked = false
    private var isSourceView = false
    private var isDestinationView = false

    private var sourceLocationMarker: Marker? = null
    private var destinationLocationMarker: Marker? = null

    private var mapView: View? = null

    private var toggle: ActionBarDrawerToggle? = null

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        toolbar.title = getString(R.string.select_pickup_location)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        mapView = mapFragment!!.view


        progressDialog = ProgressDialog(this)
        progressDialog!!.setCancelable(false)

        databaseManager = DatabaseManager()

        destSearcher   = fragmentManager.findFragmentById(R.id.dest_searchBar)   as PlaceAutocompleteFragment
        sourceSearcher = fragmentManager.findFragmentById(R.id.source_searchBar) as PlaceAutocompleteFragment

        progressBar = findViewById(R.id.progressbarLatLng)

        val c = Calendar.getInstance()
        mHour = c[Calendar.HOUR_OF_DAY]
        mMinute = c[Calendar.MINUTE]

        val str = "$mHour:$mMinute"
        txtTime.text = str

        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val str2 = mDay.toString() + "-" + (mMonth + 1) + "-" + mYear
        txtDate.text = str2

        val filter = AutocompleteFilter.Builder().setCountry("pk").build()

        destSearcher.setHint("Where you want to go?")
        sourceSearcher.setHint("Where you want to start?")
        destSearcher.setFilter(filter)
        sourceSearcher.setFilter(filter)

        mGps.setOnClickListener(this)
        llPickupTime.setOnClickListener(this)
        llPickupDate.setOnClickListener(this)
        sourceGps.setOnClickListener(this)
        destinationGps.setOnClickListener(this)
        btnPickDestination.setOnClickListener(this)
        btnPickSource.setOnClickListener(this)

        sourceSearcher.setOnPlaceSelectedListener(this)
        destSearcher.setOnPlaceSelectedListener(this)

        setSupportActionBar(toolbar)

        val headerView = navigationView!!.getHeaderView(0)
        val navUsername = headerView.findViewById<TextView>(R.id.navUsername)
        val navEmail = headerView.findViewById<TextView>(R.id.navEmail)
        try {
            navUsername.text = currentUser!!.name
            navEmail.text = currentUser!!.email
        }catch (e:java.lang.Exception){}


        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawerLayout!!.addDrawerListener(toggle!!)
        toggle!!.syncState()

        navigationView!!.setNavigationItemSelectedListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mGps -> {
                Log.d(tag, "onClick: clicked gps icon")
                try {
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrent,Constants.DEFAULT_ZOOM))
                    //mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(33.649053, 72.968686), Constants.DEFAULT_ZOOM))
                } catch (ignored: Exception) {}
            }
            R.id.llPickupTime -> {
                val c = Calendar.getInstance()
                mHour = c[Calendar.HOUR_OF_DAY]
                mMinute = c[Calendar.MINUTE]
                val timePickerDialog = TimePickerDialog(
                    this@BaseActivity,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        val str = "$hourOfDay:$minute"
                        txtTime.text = str
                    }, mHour, mMinute, false
                )
                timePickerDialog.show()
            }
            R.id.llPickupDate -> {
                val c = Calendar.getInstance()
                mYear = c[Calendar.YEAR]
                mMonth = c[Calendar.MONTH]
                mDay = c[Calendar.DAY_OF_MONTH]

                val datePickerDialog = DatePickerDialog(
                    this@BaseActivity,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        val str = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                        txtDate.text = str
                    }, mYear, mMonth, mDay
                )
                datePickerDialog.show()
            }
            R.id.btnPickDestination -> {
                isDestinationView = true
                if (latLngDest == null || txtTime == null)
                    Toast.makeText(this@BaseActivity, "please give your drop off location", Toast.LENGTH_LONG).show()
                else {
                    btnPickDestination.visibility = View.GONE
                    btnNext.visibility = View.VISIBLE
                    destinationLo.visibility = View.GONE
                    txtDestinationAddress.visibility = View.VISIBLE
                    destinationPin.visibility = View.VISIBLE
                    destinationGps.visibility = View.VISIBLE
                    val str = Util.getSafeSubstring(destinationName!!, Constants.txtMaxLength) + "..."
                    txtDestinationAddress.text = str
                    llSchedule.visibility = View.VISIBLE
                    setSupportActionBar(toolbar)
                    setBackBtnOnToolbar2()
                    flag = false

                    if (destinationLocationMarker != null) {
                        destinationLocationMarker!!.remove()
                    }
                    val markerOptions = MarkerOptions()

                    markerOptions.position(latLngDest!!)
                    markerOptions.title(Util.getSafeSubstring(destinationName!!, Constants.txtMaxLength) + "...")
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_b))
                    if (latLngDest != null) {
                        destinationLocationMarker = mMap!!.addMarker(markerOptions)
                        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngDest, Constants.DEFAULT_ZOOM))
                    }
                    if (latLngDest != null && latLngSource != null) {
                        if (ConnectionHelper.haveNetworkConnection(this@BaseActivity))
                            FetchURL(this@BaseActivity).execute(getUrl(latLngSource!!, latLngDest!!, "driving"), "driving")
                        else
                            Toast.makeText(this@BaseActivity, "Route dose not show because of connection error", Toast.LENGTH_LONG).show()
                    }
                    isRoutePicked = true
                    mPin.visibility = View.GONE
                    val builder = LatLngBounds.builder().include(latLngSource).include(latLngDest).build()
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(builder, Constants.routePadding))
                    GetDirectionsData().execute(getUrl(latLngSource!!, latLngDest!!, "driving"), this)
                }
            }
            R.id.btnPickSource -> {
                isSourceView = true
                if (latLngSource == null)
                    Toast.makeText(this@BaseActivity, "please give pickup location", Toast.LENGTH_LONG).show()
                else {
                    isRoutePicked = false
                    mPin.visibility = View.VISIBLE
                    //destEmptyView.setVisibility(View.GONE);
                    llDestSearchbar.visibility = View.VISIBLE
                    btnPickSource.visibility = View.GONE
                    btnPickDestination.visibility = View.VISIBLE
                    source_lo.visibility = View.GONE
                    txtSourceAddress.visibility = View.VISIBLE
                    sourcePin.visibility = View.VISIBLE
                    sourceGps.visibility = View.VISIBLE

                    val s = Util.getSafeSubstring(sourceName!!, Constants.txtMaxLength) + "..."

                    txtSourceAddress.text = s
                    setSupportActionBar(toolbar)
                    setBackBtnOnToolbar1()
                    isSourceConfirm = true
                    flag = true
                    flagSource = false
                    if (sourceLocationMarker != null) {
                        sourceLocationMarker!!.remove()
                    }
                    val markerOptions = MarkerOptions()
                    markerOptions.position(latLngSource!!)
                    markerOptions.title(Util.getSafeSubstring(sourceName!!, Constants.txtMaxLength) + "...")
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_a))
                    if (latLngSource != null) {
                        sourceLocationMarker = mMap!!.addMarker(markerOptions)
                        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngSource, Constants.DEFAULT_ZOOM))
                    }
                }
            }
        }
    }


    override fun onTaskDone(vararg values: Any?) {
        if (currentPolyline != null) currentPolyline!!.remove()
        currentPolyline = mMap!!.addPolyline(values[0] as PolylineOptions?)
    }

    override fun onPlaceSelected(place: Place?) {
        if (place!! == sourceSearcher) {
            flag = true
            flagSource = false
            sourcePlace = place
            latLngSource = sourcePlace!!.latLng
            sourceName = sourcePlace!!.name.toString()
            if (sourceLocationMarker != null) {
                sourceLocationMarker!!.remove()
            }
            if (latLngSource != null) {
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngSource, Constants.DEFAULT_ZOOM))
            }
        } else {
            flag = false
            destinationPlace = place
            latLngDest = destinationPlace!!.latLng
            destinationName = place.name.toString()
            if (destinationLocationMarker != null) {
                destinationLocationMarker!!.remove()
            }
            if (latLngDest != null) {
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngDest, Constants.DEFAULT_ZOOM))
            }
        }
    }

    override fun onError(p0: Status?) {}

    override fun onCallback(duration: String?, distance: String?) {
        txtDuration.text = duration
        val str = "($distance)"
        txtDistance.text = str
    }

    private fun setBackBtnOnToolbar1() {
        toolbar.setNavigationIcon(R.drawable.icon_back)
        toolbar.title = getString(R.string.select_dropoff_location)
        toolbar.setNavigationOnClickListener { makeBackBtn1Working() }
    }

    private fun setBackBtnOnToolbar2() {
        toolbar.setNavigationIcon(R.drawable.icon_back)
        toolbar.setTitle(R.string.title_activity_main)
        toolbar.setNavigationOnClickListener { makeBackBtn2Working() }
    }

    internal fun makeBackBtn1Working() {
        isSourceView = false
        toolbar.setNavigationIcon(R.drawable.icon_back)
        toolbar.title = getString(R.string.select_dropoff_location)

        llDestSearchbar.visibility = View.GONE
        btnPickSource.visibility = View.VISIBLE
        btnPickDestination.visibility = View.GONE
        source_lo.visibility = View.VISIBLE
        txtSourceAddress.visibility = View.GONE
        sourcePin.visibility = View.GONE
        sourceGps.visibility = View.GONE

        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.select_pickup_location)

        val toggle = ActionBarDrawerToggle(
            this@BaseActivity,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        isSourceConfirm = false
        flag = false
        flagSource = true
        sourceLocationMarker?.remove()
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngSource, Constants.DEFAULT_ZOOM))
        latLngSource = null
    }

    internal fun makeBackBtn2Working() {
        isDestinationView = false
        toolbar.setNavigationIcon(R.drawable.icon_back)
        toolbar.title = getString(R.string.title_activity_main)
        btnPickDestination.visibility = View.VISIBLE
        btnNext.visibility = View.GONE
        //btnTimePicker.setVisibility(View.GONE)
        destinationLo.visibility = View.VISIBLE
        txtDestinationAddress.visibility = View.GONE
        destinationPin.visibility = View.GONE
        destinationGps.visibility = View.GONE
        llSchedule.visibility = View.GONE
        txtDuration.text = "......"
        txtDistance.text = "......"
        setSupportActionBar(toolbar)
        setBackBtnOnToolbar1()
        isRoutePicked = false
        mPin.visibility = View.VISIBLE
        isSourceConfirm = true
        flag = true
        flagSource = false
        if (destinationLocationMarker != null) {
            destinationLocationMarker!!.remove()
        }
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngDest, Constants.DEFAULT_ZOOM))
        try {
            if (ConnectionHelper.haveNetworkConnection(this@BaseActivity)) currentPolyline!!.remove()
        } catch (e: Exception) {}
        latLngDest = null
    }

    override fun onCameraMove() {
        if (!isRoutePicked) {
            mPin.visibility = View.GONE
            progressBar!!.visibility = View.VISIBLE
        }
    }

    override fun onCameraIdle() {
        if (!isRoutePicked) {
            if (ConnectionHelper.haveNetworkConnection(this@BaseActivity)) {
                mPin.visibility = View.VISIBLE
                progressBar!!.visibility = View.GONE
                var currentMarkerLocation = mMap!!.cameraPosition.target
                println("$currentMarkerLocation********")//
                if (currentMarkerLocation == LatLng(0.0, 0.0)) {
                    currentMarkerLocation = LatLng(33.6847118, 73.004479)
                }
                try {
                    //val add = applicationContext.getAddressFromLatLng(currentMarkerLocation)
                    val geocode = Geocoder(this, Locale.getDefault())
                    try {
                        val addresses = geocode.getFromLocation(currentMarkerLocation.latitude, currentMarkerLocation.longitude, 1)
                        try {
                            val obj = addresses[0]
                            add = obj.getAddressLine(0)
                        } catch (e: java.lang.Exception) {}
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
                    }
                    if (isSourceConfirm) {
                        destinationName = add
                        destSearcher.setText(destinationName)
                        latLngDest = currentMarkerLocation
                    } else {
                        sourceName = add
                        sourceSearcher.setText(sourceName)
                        latLngSource = currentMarkerLocation
                    }
                }catch (e: IOException){
                    e.printStackTrace()
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
                }



            } else Toast.makeText(this@BaseActivity, "Connection error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (isSourceView && !isDestinationView)
            makeBackBtn1Working()
        else if (isDestinationView && isSourceView)
            makeBackBtn2Working()
        else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to exit application")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    moveTaskToBack(true)
                    Process.killProcess(Process.myPid())
                    exitProcess(1)
                }
                .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.menuLogout) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    progressDialog!!.setMessage("Please wait..")
                    progressDialog!!.show()
                    AuthenticationManager().signOut()
                    PushNotificationManager().unSubscribeNotification("d-" + currentUser!!.name)
                    startActivity(Intent(this@BaseActivity, SplashActivity::class.java))
                    finish()
                }
                .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }
        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        super.onMapReady(googleMap)
        mMap!!.setOnCameraMoveListener(this)
        mMap!!.setOnCameraIdleListener(this)
    }
}