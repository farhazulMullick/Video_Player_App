package com.farhazulmullick.core_ui.extensions

import android.content.Context
import android.net.Uri
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Context.getBitmap(uri: Uri) = withContext(Dispatchers.IO) {
    try {
        Glide.with(this@getBitmap)
            .asBitmap()
            .load(uri)
            .submit(512, 512)
            .get()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}