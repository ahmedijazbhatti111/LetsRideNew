package com.example.letsridenew

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.letsridenew.SplashActivity.Companion.currentUser
import com.example.letsridenew.data.AuthenticationManager
import com.example.letsridenew.data.DatabaseManager
import com.example.letsridenew.models.User
import com.example.letsridenew.models.Vehicle
import com.example.letsridenew.utils.Constants
import com.example.letsridenew.utils.interfaces.MyUserCallback
import com.example.letsridenew.utils.interfaces.OnUserChangeCallback
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.system.exitProcess

class AuthActivity : AppCompatActivity() ,View.OnClickListener{

    private var progressBar: ProgressDialog? = null
    private var type: String? = null
    private var isSignInClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressBar = ProgressDialog(this)
        progressBar!!.setCancelable(false)

        setSupportActionBar(authenticationToolbar)

        textViewSignup.setOnClickListener(this)
        buttonLogin.setOnClickListener(this)
        buttonSignUp.setOnClickListener(this)
        textViewLogin.setOnClickListener(this)
        lLayoutBtnDriver.setOnClickListener(this)
        lLayoutBtnPassenger.setOnClickListener(this)

        userTypeTxt!!.text = getString(R.string.your_account_type)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.lLayoutBtnDriver->{
                type = Constants.DRIVER
                lLayoutBtnDriver.visibility = View.GONE
                lLayoutBtnDriverSolid.visibility = View.VISIBLE
                lLayoutBtnPassenger.visibility = View.VISIBLE
                lLayoutBtnPassengerSolid.visibility = View.GONE
                val str = "Hello $type!"
                userTypeTxt!!.text = str
                llVehicleInfo.visibility = View.VISIBLE
            }
            R.id.lLayoutBtnPassenger->{
                type = Constants.PASSENGER
                lLayoutBtnDriver.visibility = View.VISIBLE
                lLayoutBtnDriverSolid.visibility = View.GONE
                lLayoutBtnPassenger.visibility = View.GONE
                lLayoutBtnPassengerSolid.visibility = View.VISIBLE
                val str = "Hello $type!"
                userTypeTxt!!.text = str
                llVehicleInfo.visibility = View.GONE
            }
            R.id.textViewSignup -> {
                authTitle!!.text = getString(R.string.create_account)
                lLayoutSignIn.visibility = View.VISIBLE
                lLayoutLogin.visibility = View.GONE
                isSignInClick = true
                //toolbar.setTitle(R.string.title_sign_up);
                authenticationToolbar.navigationIcon = ResourcesCompat.getDrawable(resources,R.drawable.icon_left_arrow,theme)
                authenticationToolbar.setNavigationOnClickListener {
                    lLayoutSignIn.visibility = View.GONE
                    lLayoutLogin.visibility = View.VISIBLE
                    isSignInClick = false
                    authTitle!!.text = getString(R.string.choose_account_type)
                    //toolbar.setTitle(R.string.title_sign_in);
                    authenticationToolbar.navigationIcon = null
                }
            }
            R.id.buttonLogin -> if (type == null) {
                Toast.makeText(
                    applicationContext,
                    "Please choose Account type",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                userLogin()
            }
            R.id.buttonSignUp -> if (type == null) {
                Toast.makeText(
                    applicationContext,
                    "Please choose Account type",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                registerUser()
            }
            R.id.textViewLogin -> {
                lLayoutSignIn.visibility = View.GONE
                lLayoutLogin.visibility = View.VISIBLE
                isSignInClick = false
                authTitle!!.text = getString(R.string.chooseAccountType)
                authenticationToolbar.navigationIcon = null
            }
        }
    }

    private fun registerUser() {
        val email: String = sEditTextEmail.text.toString().trim { it <= ' ' }
        val password: String = sEditTextPassword.text.toString().trim { it <= ' ' }
        val username = editTextUserName.text.toString().trim { it <= ' ' }
        val phoneNo = editTextPhoneNo.text.toString().trim { it <= ' ' }
        val vSeats: String = noOfSeatsInVehicle.text.toString().trim { it <= ' ' }
        val vNumber: String = vehicleNo.text.toString().trim { it <= ' ' }
        val vName: String = vehicleName.text.toString().trim { it <= ' ' }
        if (username.isEmpty()) {
            editTextUserName.error = "User name is required"
            editTextUserName.requestFocus()
            return
        }
        if (phoneNo.isEmpty()) {
            editTextPhoneNo.error = "Address is required"
            editTextPhoneNo.requestFocus()
            return
        }
        if (email.isEmpty()) {
            sEditTextEmail.error = "Email is required"
            sEditTextEmail.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            sEditTextEmail.error = "Please enter a valid email"
            sEditTextEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            sEditTextPassword.error = "Password is required"
            sEditTextPassword.requestFocus()
            return
        }
        if (password.length < 6) {
            sEditTextPassword.error = "Minimum lenght of password should be 6"
            sEditTextPassword.requestFocus()
            return
        }
        if (type == "driver") {
            if (vName.isEmpty()) {
                vehicleName.error = "Vehicle name is required"
                vehicleName.requestFocus()
                return
            }
            if (vNumber.isEmpty()) {
                vehicleNo.error = "Vehicle number is required"
                vehicleNo.requestFocus()
                return
            }
            if (vSeats.isEmpty()) {
                noOfSeatsInVehicle.error = "Vehicle no of seats are required"
                noOfSeatsInVehicle.requestFocus()
                return
            }
        }
        progressBar!!.setMessage("Create account please wait..")
        progressBar!!.show()
        AuthenticationManager().createUserWithEmailAndPassword(
            email,
            password,
            object : OnUserChangeCallback {
                override fun onSuccessCallback(user_id: String?, message: String?) {
                    if (user_id != null) {
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                        val vType: String = spTypeOfVehicle.selectedItem.toString().trim { it <= ' ' }
                        val vColor: String = spColorOfVehicle.selectedItem.toString().trim { it <= ' ' }
                        val vehicle: Vehicle?
                        if (type == "driver") {
                            vehicle = Vehicle(user_id, vName, vType, vNumber, vSeats, vColor)
                            currentUser = User(user_id, username, email, phoneNo, password, type, vehicle, false)
                            DatabaseManager().insertVehicle(vehicle)
                        } else {
                            currentUser = User(user_id, username, email, phoneNo, password, type, false)
                        }
                        DatabaseManager().insertUser(currentUser!!, object : OnUserChangeCallback {
                            override fun onSuccessCallback(
                                user_id: String?,
                                message: String?
                            ) {
                                progressBar!!.dismiss()
                                loginUser(email, password)
                            }
                        })
                    } else {
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun userLogin() {
        val email = editTextEmail.text.toString().trim { it <= ' ' }
        val password = editTextPassword.text.toString().trim { it <= ' ' }
        if (email.isEmpty()) {
            editTextEmail.error = "Email is required"
            editTextEmail.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Please enter a valid email"
            editTextEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            editTextPassword.error = "Password is required"
            editTextPassword.requestFocus()
            return
        }
        if (password.length < 6) {
            editTextPassword.error = "Minimum length of password should be 6"
            editTextPassword.requestFocus()
            return
        }
        loginUser(email, password)
    }

    private fun loginUser(email: String, password: String) {
        progressBar!!.setMessage("Attempt login please wait..")
        progressBar!!.show()

        AuthenticationManager().signInWithEmailAndPassword(email, password, object : OnUserChangeCallback {
            override fun onSuccessCallback(user_id: String?, message: String?) {
                if (user_id != null) {
                    DatabaseManager().readUser(user_id, object : MyUserCallback {
                        override fun onCallback(user: User?) {
                            currentUser = user
                            currentUser?.isLogin=true
                            println("login true true....")
                            DatabaseManager().insertUser(
                                currentUser!!,
                                object : OnUserChangeCallback {
                                    override fun onSuccessCallback(
                                            user_id: String?,
                                            message: String?
                                    ) {
                                        if (currentUser?.type.equals(type)) {
                                            if(currentUser!!.type == Constants.DRIVER){
                                                startActivity(Intent(this@AuthActivity, DriverActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                            }else{
                                                startActivity(Intent(this@AuthActivity, PassengerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                            }
                                            progressBar!!.dismiss()
                                            println("login true true....")
                                            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                                        }
                                        else {
                                            println("-----------------" + currentUser!!.type.toString() + "----------------")
                                            Toast.makeText(applicationContext, "This account is already registered with this user type .You are unable to use this account as another user.", Toast.LENGTH_SHORT).show()
                                            progressBar!!.dismiss()
                                            AuthenticationManager().signOut()
                                            showDialogOnInvalidUserType(currentUser!!.type.toString())
                                        }
                                    }
                                })
                        }
                    })
                }else{
                    progressBar!!.dismiss()
                    Toast.makeText(this@AuthActivity,"$message",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showDialogOnInvalidUserType(type: String) {
        val alertDialog : AlertDialog.Builder = AlertDialog.Builder(this@AuthActivity)
        alertDialog.setMessage("This account is already registered with $type.\nYou are unable to use this account as another user.")
            .setCancelable(false)
            .setNegativeButton("Okay"
            ) { dialog, _ -> dialog.cancel() }
        alertDialog.create().show()
    }

    override fun onBackPressed() {
        if (isSignInClick) {
            lLayoutSignIn.visibility = View.GONE
            lLayoutLogin.visibility = View.VISIBLE
            isSignInClick = false
            authTitle!!.text = getString(R.string.choose_account_type)
            authenticationToolbar.navigationIcon = null
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to exit application")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { _, _ ->
                    moveTaskToBack(true)
                    exitProcess(1)
                }
                .setNegativeButton(
                    "No"
                ) { dialog, _ -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }
    }
}