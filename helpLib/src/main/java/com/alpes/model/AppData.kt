package com.alpes.model

import android.content.Intent
import android.os.Parcelable
import com.alpes.utils.Wrapper
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppData(val intent: Intent) : Parcelable