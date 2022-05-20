package com.alpes.helplib

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.FrameLayout
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import java.util.*
import kotlin.collections.HashMap


class HelpActivity : AppCompatActivity() {
    private lateinit var linear: FrameLayout
    private var fаlse: Boolean = false
    private lateinit var cats: WebView
    private var text: String = ""
    private var keyAppsFlyer: String? = null
    private var keyFb: String? = null
    private var keyFbSecret: String? = null
    private lateinit var intentMain: Intent
    private lateinit var intentHelp: Intent
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        intentHelp = intent
        sharedPreferences = getSharedPreferences("last", Context.MODE_PRIVATE)
        linear = findViewById(R.id.linear)
        cats = WebView(this)
        settingApp()
        cats.webViewClient = WebViewClient()
        cats.webChromeClient = UtilsInfo(this)
        cats.apply {
            settings.javaScriptEnabled = fаlse
            settings.javaScriptCanOpenWindowsAutomatically = fаlse
            settings.domStorageEnabled = fаlse
        }
        text += intentHelp.getStringExtra("link")
        keyAppsFlyer = intentHelp.getStringExtra("aps")
        keyFb = intentHelp.getStringExtra("fbAppId")
        keyFbSecret = intentHelp.getStringExtra("fbClientSecret")
        Log.d("jopa", "$text, $keyAppsFlyer")
        if (Build.VERSION.SDK_INT >= 24) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(cats, true)
        } else {
            CookieManager.getInstance().setAcceptCookie(true)
        }
        cats.setBackgroundColor(Color.BLACK)
        cats.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action === MotionEvent.ACTION_UP) {
                while (cats.canGoBack())
                    cats.goBack()
                return@OnKeyListener true
            }
            false
        })
        Log.d("spectra", text)
        linear.addView(cats)
        loadingView(cats)

        if(sharedPreferences.contains("last")
            && sharedPreferences.getString("last", "")!!.isNotEmpty()){
            cats.loadUrl(sharedPreferences.getString("last", text)!!)
        } else {
            if (keyFb == null) {
                ld(text)
            }else {
                loadFb()
            }
        }
    }

    private fun loadingView(webView: WebView) {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url.toString()
                Log.d("spectra", url)
                if (url.contains("almanach", true)) {
                    this@HelpActivity.onBackPressed()
                }

                return false
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                if (url!!.contains("almanach", true)) {
                    //todo
                } else {
                    editor.putString("last", url)
                    editor.apply()
                }
            }
        }
    }

    companion object {
        var message: ValueCallback<Uri>? = null
        var messageArray: ValueCallback<Array<Uri>>? = null
        val REQUEST_SELECT_FILE = 921
        val FILECHOOSER_RESULTCODE = 72
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (messageArray == null)
                    return
                messageArray!!.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(
                        resultCode,
                        data
                    )
                )
                messageArray = null
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == message)
                return
            val result =
                if (data == null || resultCode != AppCompatActivity.RESULT_OK) null else data.data
            message!!.onReceiveValue(result)
            message = null
        } else {
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadFb() {
        val url = Uri.parse(text)
        val mSettings: SharedPreferences =
            getSharedPreferences("info", Context.MODE_PRIVATE)
        val fbKey = keyFb ?: return
        val fbClientSecret = keyFbSecret ?:return
        FacebookSdk.setApplicationId(fbKey)
        FacebookSdk.setClientToken(fbClientSecret)
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.sdkInitialize(applicationContext)
        AppLinkData.fetchDeferredAppLinkData(applicationContext
        ) { appLinkData -> // Process app link data
            Log.d("jopa", FacebookSdk.getApplicationId())
            Log.d("jopa", "Deep link receive$appLinkData")
            if (appLinkData != null) {
                val uri: Uri? = appLinkData.targetUri
                val pathSegments: List<String> = uri!!.pathSegments

                val builder = Uri.Builder()
                builder.scheme(url.scheme)
                    .authority(url.authority)
                    .appendPath(url.lastPathSegment)

                Log.d("jopa", pathSegments.size.toString())
                for (i in pathSegments.indices) {
                    val x: Int = i + 1
                    builder.appendQueryParameter("subid$x", pathSegments[i])
                    val e: SharedPreferences.Editor = mSettings.edit()
                    e.putString("subid$x", pathSegments[i])
                    e.apply()
                }

                val myUrl = builder.build().toString()
                Log.d("jopa", "myurl is first $myUrl")
                runOnUiThread  {
                    cats.loadUrl(myUrl)
                }

                Log.d("jopa", myUrl)
                Log.d("jopa", "fb")
            } else {
                val builder = Uri.Builder()
                builder.scheme(url.scheme)
                    .authority(url.authority)
                    .appendPath(url.lastPathSegment)
                val myUrl = builder.build().toString()
                Log.d("jopa", "myurl is second $myUrl")

                runOnUiThread {
                    cats.loadUrl(myUrl)
                }
            }
        }
    }


    private fun ld(text: String){
        val mSettings: SharedPreferences = this.getSharedPreferences("info", Context.MODE_PRIVATE)
        Log.d("jopa", mSettings.contains("info").toString() + "\n" + mSettings.all.toString())
        var counter =  1
        var str = text
        str+="/?"
        while (mSettings.contains("subid$counter")) {
            Log.d("jopa", "while")
            str+="subid$counter=${mSettings.getString("subid$counter", "")}&"
            counter++
            Log.d("jopa", "URL: $str")
            Log.d("jopa", "coubter: $counter")
        }
        if(counter!=1) {
            str.dropLast(1)
            Log.d("jopa", counter.toString())
            Log.d("jopa", "shred")
            Log.d("jopa", str)
            cats.loadUrl(str)
        }
        else{
            keyAppsFlyer?.let {
                initAppsflyer(it, str)
            } ?: cats.loadUrl(str)
        }
    }

    private fun settingApp() {
        fаlse = true
    }

    private fun initAppsflyer(devKey: String, text: String){
        Log.d("jopa", devKey)
        val conversionDataListener  = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
                for (attrName in conversionData.keys)
                    Log.d("MetLog", "Conversion attribute: " + attrName + " = " + conversionData[attrName])

                val status: String = Objects.requireNonNull(conversionData["af_status"]).toString()
                val conversionDataValues: MutableMap<String, String> = HashMap()

                if (status == "Non-organic") {
                    for ((key, value) in conversionData) {
                        if (value != null) conversionDataValues[key] = value.toString()
                    }
                }
                Log.d("jopa", " onConversionDataSuccess")
                onAppOpenAttribution(conversionDataValues)
            }

            override fun onConversionDataFail(error: String?) {
                Log.e("jopa", "error onAttributionFailure :  $error")


                runOnUiThread {
                    if (text.contains("/?/?", true)){
                        this@HelpActivity.text = text.replace("/?/?", "/?")
                    }
                    if (cats.url == null)
                        cats.loadUrl(text)
                }
            }

            override fun onAppOpenAttribution(attributionData: MutableMap<String, String>) {
                if (attributionData.isNotEmpty()) {
                    try {
                        val af_ad = attributionData["campaign"]?.split("_")?.toTypedArray()

                        val mSettings: SharedPreferences = getSharedPreferences("info", Context.MODE_PRIVATE)

                        var str = text
                        str+="/?"
                        for (i in 0..4) {
                            val x: Int = i + 1
                            str+="subid$x=${if (af_ad?.getOrNull(i) == null) "null" else af_ad[i]}&"
                            val e: SharedPreferences.Editor = mSettings.edit()
                            e.putString("subid$x", if (af_ad?.getOrNull(i) == null) "null" else af_ad[i])
                            e.apply()
                        }
                        str+="subid6=${attributionData["campaign_id"]}&"
                        val e: SharedPreferences.Editor = mSettings.edit()
                        e.putString("subid6", if (attributionData["campaign_id"] == null) "null" else attributionData["campaign_id"])
                        str+="subid7=${attributionData["adset_id"]}&"
                        e.putString("subid7", if (attributionData["adset_id"] == null) "null" else attributionData["adset_id"])
                        str+="subid8=${attributionData["ad_id"]}&"
                        e.putString("subid8", if (attributionData["ad_id"] == null) "null" else attributionData["ad_id"])
                        str+="subid9=${AppsFlyerLib.getInstance().getAppsFlyerUID(applicationContext)}"
                        e.putString("subid9", AppsFlyerLib.getInstance().getAppsFlyerUID(applicationContext))
                        e.apply()

                        Log.d("jopa", " is first $str")
                        runOnUiThread {
                            if (str.contains("/?/?", true)){
                                str = text.replace("/?/?", "/?")
                            } else cats.loadUrl(str)

                            if (cats.url == null)
                                cats.loadUrl(str)
                        }
                    } catch (e: Exception) {
                        Log.d("jopa", e.message.toString())
                        runOnUiThread {
                            if (text.contains("/?/?", true)){
                                this@HelpActivity.text = text.replace("/?/?", "/?")
                            } else cats.loadUrl(text)
                            if (cats.url == null)
                                cats.loadUrl(text)
                        }
                    }
                } else {
                    Log.d("jopa", "attribution data empty")
                    runOnUiThread {
                        if (text.contains("/?/?", true)){
                            this@HelpActivity.text = text.replace("/?/?", "/?")
                        }
                        if (cats.url == null)
                            cats.loadUrl(text)
                    }
                }
            }

            override fun onAttributionFailure(error: String?) {
                Log.e("jopa", "error onAttributionFailure :  $error")
                runOnUiThread {
                    cats.loadUrl(text)
                }
                Log.d("jopa", " is second $text")

            }
        }
        Log.d("jopa", "init0")
        AppsFlyerLib.getInstance().init(devKey, conversionDataListener, this)
        AppsFlyerLib.getInstance().setMinTimeBetweenSessions(0)
        AppsFlyerLib.getInstance().start(applicationContext)
        AppsFlyerLib.getInstance().setDebugLog(true)
    }
}

