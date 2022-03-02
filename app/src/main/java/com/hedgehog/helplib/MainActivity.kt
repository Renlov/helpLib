package com.hedgehog.helplib

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alpes.utils.initHelp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (intent.getBooleanExtra("back", true)) {
            CoroutineScope(Dispatchers.IO).launch {
                initHelp(BuildConfig.APPLICATION_ID,
                    Intent(applicationContext, MainActivity::class.java)
                )
            }
        }

    }
}