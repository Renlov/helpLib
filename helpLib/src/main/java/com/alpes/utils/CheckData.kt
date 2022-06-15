package com.alpes.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.annotation.Keep
import com.alpes.helplib.HelpActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Keep
suspend fun <T : Activity> T.initHelp(appId: String, intent: Intent) {
    try {
        val app = getApp(appId)

        Log.d("Network", app.toString())
        if (app.source != null)
            startActivity(
                Intent(this, HelpActivity::class.java)
                    .withArguments(app, intent)
            ) else return
    } catch (e: Exception) {
        Log.e("Network", e.message.toString())
        return
    }
}


suspend fun getApp(bundle: String): App = withContext(Dispatchers.IO) {
    Networking.greySourceApi.getApp(bundle)
}

fun Intent.withArguments(app: App, intent: Intent): Intent {
    return this.apply {
        putExtra("app", intent)
        putExtra("link", app.source)
        putExtra("aps", app.appsFlyer)
        putExtra("fbAppId", app.fbAppId)
        putExtra("fbClientSecret", app.fbClientSecret)
    }
}



