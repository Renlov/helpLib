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
import bolts.AppLinks
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
    private lateinit var intentHelp: Intent
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
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

        linear.addView(cats)
        loadingView(cats)

        if (sharedPreferences.contains("last")
            && sharedPreferences.getString("last", "")!!.isNotEmpty()
        ) {
            cats.loadUrl(sharedPreferences.getString("last", text)!!)
        } else {
            ld(text)
        }
    }

    private fun loadingView(webView: WebView) {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url.toString()
                Log.d(TAG, "loadingView : url is $url")
                if (url.contains("almanach", true)) {
                    Log.d(TAG, "almanach")
                    this@HelpActivity.onBackPressed()
                }
                if (url.startsWith("sms:")) {
                    view.context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    )
                    Log.d(TAG, "sms")
                    return true
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
        private const val TAG = "HelpActivity"
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

    private fun ld(text: String) {
        val mSettings: SharedPreferences = this.getSharedPreferences("info", Context.MODE_PRIVATE)
        Log.d(
            TAG,
            "ld fun mSettings : ${mSettings.contains("info").toString() + "\n" + mSettings.all}"
        )
        var counter = 1
        var str = text
        if (mSettings.contains("subid$counter"))
            str += "/?"
        while (mSettings.contains("subid$counter")) {
            str += "subid$counter=${mSettings.getString("subid$counter", "")}&"
            counter++
        }
        if (counter != 1) {
            str.dropLast(1)
            Log.d(TAG, "loadUrl = $str")
            cats.loadUrl(str)
        } else {
                cats.loadUrl(str)
            }
        }
    private fun settingApp() {
        fаlse = true
    }

}