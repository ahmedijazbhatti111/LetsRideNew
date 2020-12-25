package com.example.letsridenew.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class NotificationHelper(
    NOTIFICATION_TITLE: String?,
    NOTIFICATION_MESSAGE: String?,
    TOPIC: String
) {
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey = "key=" + "AAAAE34-geQ:APA91bHOzJKYdx4YOYkv0Pq2-xByUsvu9Ihs32i4o8KJ1udCihO2Gy5Nc-iBXlRmngRr_-f5vB9MVSMA2UR6rCmT7OMmng9aUfVYJgS6uwJs1WJ0UEIp8CUlU1R8TJJI9cjlPdCUv55L"
    private val contentType = "application/json"
    val TAG = "NOTIFICATION TAG"

    private val notificationResponce: JSONObject = JSONObject()
    fun sendNotification(c: Context) {
        val jsonObjectRequest: JsonObjectRequest =
            object : JsonObjectRequest(FCM_API, notificationResponce,
                Response.Listener { response -> Log.i(TAG, "onResponse: $response") },
                Response.ErrorListener {
                    Toast.makeText(c, "Request error", Toast.LENGTH_LONG).show()
                    Log.i(TAG, "onErrorResponse: Didn't work")
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> =
                        HashMap()
                    params["Authorization"] = serverKey
                    params["Content-Type"] = contentType
                    return params
                }
            }
        NotificationRequestSingleton.getInstance(c)?.addToRequestQueue(jsonObjectRequest)
    }

    init {
        val notifcationBody = JSONObject()
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE)
            notifcationBody.put("message", NOTIFICATION_MESSAGE)
            notificationResponce.put("to", "/topics/d-$TOPIC")
            notificationResponce.put("data", notifcationBody)
        } catch (e: JSONException) {
            Log.e(TAG, "onCreate: " + e.message)
        }
    }
}