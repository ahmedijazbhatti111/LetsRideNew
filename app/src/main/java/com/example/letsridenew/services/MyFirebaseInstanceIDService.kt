package com.example.letsridenew.services

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {
    // private static final String SUBSCRIBE_TO = "userABC";
    override fun onTokenRefresh() {
        /*
          This method is invoked whenever the token refreshes
          OPTIONAL: If you want to send messages to this application instance
          or manage this apps subscriptions on the server side,
          you can send this token to your server.
        */
        val token = FirebaseInstanceId.getInstance().token

        // Once the token is generated, subscribe to topic with the userId
        //FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO);
        Log.i(
            TAG,
            "onTokenRefresh completed with token: $token"
        )
    }

    companion object {
        private const val TAG = "mFirebaseIIDService"
    }
}