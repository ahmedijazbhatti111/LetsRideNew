package com.example.letsridenew

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.letsridenew.SplashActivity.Companion.currentUser
import com.example.letsridenew.data.DatabaseManager
import com.example.letsridenew.data.PushNotificationManager
import com.example.letsridenew.models.Location
import com.example.letsridenew.models.Schedule
import kotlinx.android.synthetic.main.content_base.*

class PassengerActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_passenger)

        PushNotificationManager().subscribeNotification("p-" + currentUser!!.uid)

        btnNext.text = getText(R.string.find_someone)
        btnNext.setOnClickListener {
            if (latLngDest == null || latLngSource == null || txtTime == null) Toast.makeText(
                this@PassengerActivity,
                "please give all information",
                Toast.LENGTH_LONG
            ).show() else {
                val schedule = Schedule(
                    Location(sourceName, latLngSource),
                    Location(destinationName, latLngDest),
                    txtTime.text.toString().trim { it <= ' ' },
                    txtDate.text.toString().trim { it <= ' ' },
                    currentUser
                )
                DatabaseManager().insertUserSchedule(schedule)
                val intent: Intent =
                    Intent(applicationContext, ScheduleListActivity::class.java)
                        .putExtra("latLngSource", latLngSource)
                        .putExtra("latLngDest", latLngDest)
                        .putExtra("pTime", txtTime.text.toString())
                startActivity(intent)
            }
        }
    }
}