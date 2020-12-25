package com.example.letsridenew

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.letsridenew.data.AuthenticationManager
import com.example.letsridenew.data.DatabaseManager
import com.example.letsridenew.models.User
import com.example.letsridenew.utils.ConnectionHelper
import com.example.letsridenew.utils.Constants.REQUEST_LOCATION_CODE
import com.example.letsridenew.utils.interfaces.MyUserCallback
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    companion object{
        var currentUser: User? = null
    }

    private var mAuth: AuthenticationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mAuth = AuthenticationManager()

        btnRetry!!.setOnClickListener {
            onStart()
        }
    }

    override fun onStart() {
        super.onStart()
        if (ConnectionHelper.haveNetworkConnection(this)) {
            dismissNoInternet()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission()
            }
            if (mAuth!!.user != null) {
                val cUserId: String = mAuth!!.user!!.uid
                DatabaseManager().readUser(cUserId, object : MyUserCallback {
                    override fun onCallback(user: User?) {
                        currentUser = user
                        val intent: Intent = if (currentUser!!.type.equals("driver")) {
                            println("---------------main-----------------")
                            Intent(this@SplashActivity, DriverActivity::class.java)
                        } else {
                            Intent(this@SplashActivity, PassengerActivity::class.java)
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                })
            } else {
                println("Please login..Please login..Please login..")
                Toast.makeText(this, "Please login..", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
            }
        } else {
            showNoInternet()
            Toast.makeText(this, "Please enable network connection", Toast.LENGTH_LONG).show()
        }
    }

    private fun showNoInternet(){
        l_layout_no_internet!!.visibility = View.VISIBLE
        startup_title!!.visibility = View.GONE
        startup_progressbar!!.visibility = View.GONE
    }
    private fun dismissNoInternet(){
        l_layout_no_internet!!.visibility = View.GONE
        startup_title!!.visibility = View.VISIBLE
        startup_progressbar!!.visibility = View.VISIBLE
    }

    private fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE)
            }
            false
        } else {
            true
        }
    }
}