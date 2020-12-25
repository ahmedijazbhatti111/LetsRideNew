package com.example.letsridenew.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.letsridenew.R

class ReqDriverDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    init {
        setContentView(R.layout.route_dialog)
    }
}