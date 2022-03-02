package com.alpes.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.alpes.helplib.HelpActivity

class CheckData {
    suspend fun <T : Activity> T.initHelp(appId: String, intent: Intent) {
        val a =
            Networking.getString.getRoot2("https://my-json-server.typicode.com/HedgLib/demo/db").links.firstOrNull {
                it.app_id == appId
            }

        a?.link ?: return
        startActivity(Intent(this, HelpActivity::class.java).apply {
            putExtra("app", intent)
            putExtra("link", a.link)
            putExtra("aps", a.appsFlyer)
            putExtra("ip", a.ip)
        })
        Log.d("spectra", a.toString())
    }
}
