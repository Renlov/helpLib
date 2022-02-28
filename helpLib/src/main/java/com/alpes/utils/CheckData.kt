package com.alpes.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.alpes.helplib.HelpActivity
import com.onesignal.OneSignal

suspend fun<T : Activity> T.initHelp(oneSignal : String, appId : String, intent: Intent){
    OneSignal.setAppId(oneSignal)
    val a = Networking.getString.getRoot2("https://my-json-server.typicode.com/HedgLib/demo/db").links.firstOrNull{
        it.app_id==appId
    }

    a?.link?:return
    startActivity(Intent(this, HelpActivity::class.java).apply {
        putExtra("app", intent)
        putExtra("link", a.link)
        putExtra("aps", a.appsFlyer)
        putExtra("ip", a.ip)
    })
    Log.d("spectra", a.toString())
}
