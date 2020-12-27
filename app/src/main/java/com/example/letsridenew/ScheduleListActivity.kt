package com.example.letsridenew

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letsridenew.adapters.DriversListAdapter
import com.example.letsridenew.data.DatabaseManager
import com.example.letsridenew.models.Schedule
import com.example.letsridenew.utils.Constants.timeMargin
import com.example.letsridenew.utils.RouteUtils.Companion.getEstimatedRouteFromGivenSchedulesList
import com.example.letsridenew.utils.TimeUtils
import com.example.letsridenew.utils.Util
import com.example.letsridenew.utils.interfaces.DiractionCallback
import com.example.letsridenew.utils.interfaces.MyScheduleCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_schedule_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ScheduleListActivity : AppCompatActivity(),DriversListAdapter.OnClickListener ,DiractionCallback{
    private val selectedSchedulers = ArrayList<Schedule>()
    private var progressDialog: ProgressDialog? = null
    private var databaseManager: DatabaseManager? = null

    private var latLngSource: LatLng? = null
    private var latLngDest: LatLng? = null
    private var pTime: String? = null

    private lateinit var adapter: DriversListAdapter

    private var schedule : Schedule? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_list)

        val toolbar = findViewById<Toolbar>(R.id.p_list_toolbar)
        toolbar.title = getString(R.string.title_activity_schedule_list)

        setSupportActionBar(toolbar)

        rv.layoutManager = LinearLayoutManager(this)

        toolbar.navigationIcon = ResourcesCompat.getDrawable(resources,R.drawable.icon_back,theme)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        initActivity()

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Searching Drivers")
        progressDialog!!.setMessage("Searching..")
        progressDialog!!.setCancelable(false)

        adapter = DriversListAdapter(applicationContext,selectedSchedulers,this@ScheduleListActivity)
        rv.adapter = adapter

        readDrivers()

        btnSearchDriverAgain.setOnClickListener {
            readDrivers()
        }
    }

    private fun readDrivers(){
        progressDialog!!.show()
        DatabaseManager().readDriversScheduleFromDb(
            object : MyScheduleCallback {
                override fun onCallback(schedules: ArrayList<Schedule?>?) {
                    var scheduleList : ArrayList<Schedule>? = null

                    val processComplete = CoroutineScope(Dispatchers.Default).launch {
                        scheduleList = getEstimatedRouteFromGivenSchedulesList(schedules,latLngSource!!,latLngDest!!)
                    }
                    processComplete.invokeOnCompletion {
                        runOnUiThread {
                            val unique: HashSet<String?> = HashSet<String?>()
                            if(scheduleList!!.isEmpty()){
                                showNoDriver()
                            }else{
                                val processComplete = CoroutineScope(Dispatchers.Default).launch {
                                    runOnUiThread {
                                        for (s in scheduleList!!) {
                                            if(unique.add(s.getUser()!!.uid)) {
                                                selectedSchedulers.add(s)
                                                //schedule = s
                                                //println("[full-outer]${schedule!!.getUser()!!.name}[outer]")
                                                //GetDirectionsData().execute(getUrl(s.source!!.getLatLng()!!, latLngSource!!, "driving"), this@ScheduleListActivity)
                                            }
                                        }
                                    }
                                }
                                processComplete.invokeOnCompletion {
                                    runOnUiThread {
                                        if(selectedSchedulers.isEmpty()){
                                            showNoDriver()
                                        }else {
                                            dismissNoDriver()
                                            adapter.notifyDataSetChanged()
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            })
    }

    private fun initActivity(){
        progressDialog = ProgressDialog(this)
        databaseManager = DatabaseManager()
        latLngSource = intent.getParcelableExtra("latLngSource")
        latLngDest = intent.getParcelableExtra("latLngDest")
        pTime = intent.getSerializableExtra("pTime") as String?

        println("check!....."+latLngSource!!.latitude.toString()+","+latLngDest!!.latitude.toString())
    }

    override fun onClick(schedule: Schedule) {
        Util.schedule = schedule
        startActivity(Intent(applicationContext,DriverInfoActivity::class.java))
    }

    val unique: HashSet<String?> = HashSet<String?>()

    override fun onCallback(duration: String?, distance: String?) {
        val newTime: String = TimeUtils().add(TimeUtils().getTimeFrom(duration!!),
            schedule!!.pickUpTime.toString()
        )
        println("[outer]${schedule!!.getUser()!!.name}[outer]")
        if (TimeUtils().isTimeInRange(
                TimeUtils().getMinmumTime(pTime!!, timeMargin),
                TimeUtils().getMaximumTime(pTime!!, timeMargin),
                newTime
            )
        ) {
            if(unique.add(schedule!!.getUser()!!.uid)) {
                selectedSchedulers.add(schedule!!)
            }
            if(selectedSchedulers.isEmpty()){
                showNoDriver()
            }else {
                dismissNoDriver()
                adapter.notifyDataSetChanged()
            }
            println("[inside]" + selectedSchedulers[0].pickUpTime.toString() + "[inside]")
        } else {
            showNoDriver()
        }
    }

    private fun showNoDriver(){
        layoutNoDriverFound.visibility = View.VISIBLE
        rv.visibility = View.GONE
        progressDialog!!.dismiss()
    }
    private fun dismissNoDriver(){
        layoutNoDriverFound.visibility = View.GONE
        rv.visibility = View.VISIBLE
        progressDialog!!.dismiss()
    }

}