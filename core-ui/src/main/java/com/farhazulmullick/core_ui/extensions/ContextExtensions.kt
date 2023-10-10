package com.farhazulmullick.core_ui.extensions

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.LruCache
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageCache(capacity: Int) : LruCache<Uri, Bitmap>(capacity) {

    companion object {
        private val lock = object
        @Volatile
        private var cache : ImageCache? = null

        @JvmStatic
        fun getInstance(context: Context) : ImageCache {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val maxKB = am.memoryClass * 1024
            val limit = maxKB.div(8)
            return cache ?: synchronized(lock) {
                val instance = ImageCache(limit)
                cache = instance
                instance
            }
        }
    }

    override fun sizeOf(key: Uri?, value: Bitmap?): Int {
        return value?.allocationByteCount ?: 0
    }
}

suspend fun Context.getBitmap(uri: Uri) = withContext(Dispatchers.IO) {
    try {
        val cache = ImageCache.getInstance(context = this@getBitmap)
        cache.get(uri) ?: run {
            val bitmap = Glide.with(this@getBitmap)
                .asBitmap()
                .load(uri)
                .submit(512, 512)
                .get()
            cache.put(uri, bitmap)
            bitmap
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}