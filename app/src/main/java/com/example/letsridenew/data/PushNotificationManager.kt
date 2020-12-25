package com.example.letsridenew.data

import com.google.firebase.messaging.FirebaseMessaging

class PushNotificationManager {
    fun subscribeNotification(name: String?) {
        name?.let { FirebaseMessaging.getInstance().subscribeToTopic(it) }
    }

    fun unSubscribeNotification(name: String?) {
        name?.let { FirebaseMessaging.getInstance().unsubscribeFromTopic(it) }
    }
}