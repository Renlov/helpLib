package com.hedgehog.helplib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alpes.utils.initHelp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CoroutineScope(Dispatchers.IO).launch {
            initHelp("fb337ecc-10a2-4ab2-b7c2-c6be9c693107", BuildConfig.APPLICATION_ID)
        }
    }
}