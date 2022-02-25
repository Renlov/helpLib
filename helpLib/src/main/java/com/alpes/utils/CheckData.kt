package com.alpes.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.alpes.helplib.StartActivity
import com.onesignal.OneSignal

suspend fun<T : Activity> T.initHelp(oneSignal : String, appId : String){
    OneSignal.setAppId(oneSignal)
    val a = Networking.getString.getRoot2("https://my-json-server.typicode.com/HedgLib/demo/db").links.firstOrNull{
        it.app_id==appId
    }
    a?.link?:return
    startActivity(Intent(this, StartActivity::class.java))
    Log.d("spectra", a.toString())
}
