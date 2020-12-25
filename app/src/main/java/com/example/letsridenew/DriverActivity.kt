package com.example.letsridenew

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.letsridenew.SplashActivity.Companion.currentUser
import com.example.letsridenew.data.PushNotificationManager
import com.example.letsridenew.models.RideRequest
import com.example.letsridenew.models.Schedule
import com.example.letsridenew.models.User
import com.example.letsridenew.utils.Constants
import com.example.letsridenew.utils.Util
import com.example.letsridenew.utils.interfaces.MyRequestsListCallback
import com.example.letsridenew.utils.interfaces.MyUsersListCallback
import com.example.letsridenew.utils.interfaces.OnRideRequestCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.content_base.*

open class DriverActivity : BaseActivity() {

    private var fromUid: String? = ""
    private var n: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       //val rootView: View = layoutInflater.inflate(R.layout.activity_express__failed_deliveries, frameLayout)
        //setContentView(R.layout.activity_driver)
        checkRideRequest()

        PushNotificationManager().subscribeNotification("d-" + currentUser!!.uid)

        btnNext.text = getString(R.string.confirm)
        btnNext.setOnClickListener {
            if (latLngDest == null || latLngSource == null || txtTime == null) Toast.makeText(
                this@DriverActivity,
                "please give all information",
                Toast.LENGTH_LONG
            ).show() else {
                val builder =
                    AlertDialog.Builder(this@DriverActivity)
                builder.setMessage("Are you sure you want to submit this schedule")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Yes"
                    ) { dialog, _ ->
                        val schedule = Schedule(
                            com.example.letsridenew.models.Location(sourceName, latLngSource),
                            com.example.letsridenew.models.Location(destinationName, latLngDest),
                            txtTime.text.toString().trim { it <= ' ' },
                            txtDate.text.toString().trim { it <= ' ' },
                            currentUser,
                            currentPolyline!!.points
                        )
                        databaseManager!!.insertUserSchedule(schedule)
                        dialog.cancel()
                        Toast.makeText(
                            this@DriverActivity,
                            "Your Schedule is Submited",
                            Toast.LENGTH_SHORT
                        ).show()
                        Util.schedule = schedule
                        makeBackBtn2Working()
                        makeBackBtn1Working()
                        startActivity(Intent(this@DriverActivity,DriverInfoActivity::class.java))
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog, _ -> dialog.cancel() }
                val alert = builder.create()
                alert.show()
            }
        }
    }

    private fun checkRideRequest() {
        databaseManager!!.readAllRideRequests(object : MyRequestsListCallback {
            override fun onRequestsRead(rideRequests: ArrayList<RideRequest?>?) {
                println("Read all ride requests ---------")
                for (request in rideRequests!!) {
                    if (request!!.to_uid.equals(currentUser!!.uid)) {
                        println("Ride requests equals to current uid ---------")
                        databaseManager!!.readAllUsers(object : MyUsersListCallback {
                            override fun onUsersRead(users: ArrayList<User?>?) {
                                for (user in users!!) {
                                    if (user!!.uid.equals(request.from_uid) && request.req_status.equals(Constants.PENDING)) {
                                        println("Ride requests id equals to user id ---------")
                                        showRequestDialog(request,user)
                                    }
                                }
                            }
                        })
                    }
                }
            }
        })
    }

    private fun showRequestDialog(request : RideRequest,user: User) {
        val builder = AlertDialog.Builder(this@DriverActivity)
        builder.setMessage("You have new ride request from " + user.name)
            .setCancelable(false)
            .setPositiveButton("Accept") { dialog, _ ->
                println("Ride requests accepted ---------")
                request.req_status = Constants.ACCEPTED
                databaseManager!!.insertRideRequest(request, object : OnRideRequestCallback {
                        override fun onRequest(request: RideRequest?, message: String?) {
                            if (request != null) {
                                n = user.name
                                fromUid = request.from_uid
                                dialog.cancel()
                                startActivity(Intent(this@DriverActivity,StartTrackingActivity::class.java).putExtra(Constants.REQUEST,request))
                            } else {
                                Toast.makeText(this@DriverActivity, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
            .setNegativeButton("Reject") { dialog, _ ->
                request.req_status = Constants.REJECTED
                databaseManager!!.insertRideRequest(request, object : OnRideRequestCallback {
                        override fun onRequest(request: RideRequest?, message: String?) {
                            if (request != null) {
                                dialog.cancel()
                            } else {
                                Toast.makeText(this@DriverActivity , message, Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
        val alert = builder.create()
        alert.show()
    }
}