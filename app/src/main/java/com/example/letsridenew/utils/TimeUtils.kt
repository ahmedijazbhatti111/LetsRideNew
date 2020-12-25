package com.example.letsridenew.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class TimeUtils {
    companion object{
        fun getCurrentDateTime():String{
            val c: Calendar = Calendar.getInstance()
            println("Current time => " + c.getTime())

            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return df.format(c.time)
        }
    }
    fun getMinmumTime(t1: String, m: Int): String {
        val m1 = t1.substring(t1.indexOf(":") + 1).toInt()
        var h1 = t1.substring(0, t1.indexOf(":")).toInt()
        var m2 = m1 - m
        var s: String
        if (m2 < 0) {
            h1 -= 1
            m2 += 60
            s = "$h1:$m2"
        } else {
            s = "$h1:$m2"
        }
        if (h1 == -1) s = "23:$m2"
        return s
    }

    fun getMaximumTime(t1: String, m: Int): String {
        val m1 = t1.substring(t1.indexOf(":") + 1).toInt()
        var h1 = t1.substring(0, t1.indexOf(":")).toInt()
        var m2 = m1 + m
        var s: String
        if (m2 > 59) {
            h1 += 1
            m2 -= 60
            s = "$h1:$m2"
        } else {
            s = "$h1:$m2"
        }
        if (h1 == 24) s = "0:$m2"
        return s
    }

    fun isTimeInRange(
        minTime: String,
        maxTime: String,
        yTime: String
    ): Boolean {
        val t1m = minTime.substring(minTime.indexOf(":") + 1).toInt()
        var t1h = minTime.substring(0, minTime.indexOf(":")).toInt()
        val t2m = maxTime.substring(maxTime.indexOf(":") + 1).toInt()
        var t2h = maxTime.substring(0, maxTime.indexOf(":")).toInt()
        val yTimeM = yTime.substring(yTime.indexOf(":") + 1).toInt()
        var yTimeH = yTime.substring(0, yTime.indexOf(":")).toInt()
        if (t1h == 0) {
            t1h = 24
        }
        if (t2h == 0) {
            t2h = 24
        }
        if (yTimeH == 0) {
            yTimeH = 24
        }
        val t1TotleM = t1h * 60 + t1m
        val t2TotleM = t2h * 60 + t2m
        val yTimeMTotleM = yTimeH * 60 + yTimeM

        //System.out.println(t1TotleM+"  "+t2TotleM+"  "+yTimeMTotleM);
        return yTimeMTotleM in (t1TotleM + 1) until t2TotleM
    }

    fun getTimeFrom(time: String): String {
        var hours = 0
        var min = 0
        if (time.contains("hours") && time.contains("min")) {
            hours = time.substring(0, 7).replace("\\D+".toRegex(), "").trim { it <= ' ' }.toInt()
            min = time.substring(7).replace("\\D+".toRegex(), "").trim { it <= ' ' }.toInt()
            //System.out.println(hours+":"+min);
        } else if (time.contains("min")) {
            hours = 0
            min = time.replace("\\D+".toRegex(), "").trim { it <= ' ' }.toInt()
            //System.out.println(hours+":"+min);
        }
        return "$hours:$min"
    }

    fun add(t1: String, t2: String): String {
        var s: String? = null
        val t1m = t1.substring(t1.indexOf(":") + 1).toInt()
        var t1h = t1.substring(0, t1.indexOf(":")).toInt()
        val t2m = t2.substring(t2.indexOf(":") + 1).toInt()
        var t2h = t2.substring(0, t2.indexOf(":")).toInt()
        if (t1h == 0) {
            t1h = 24
        }
        if (t2h == 0) {
            t2h = 24
        }
        var totalH = t1h + t2h
        var totalM = t1m + t2m
        if (totalH == 0) {
            totalH = 24
        } else if (totalH >= 24) {
            totalH -= 24
        }
        if (totalM > 59) {
            totalH += 1
            totalM -= 60
            s = "$totalH:$totalM"
        } else {
            s = "$totalH:$totalM"
        }
        return s
    }
}