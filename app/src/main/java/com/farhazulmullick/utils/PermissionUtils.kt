package com.farhazulmullick.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

object PermissionUtils {
    fun isStoragePermissionGranted(context: Context) =
        (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)

    fun isSettingsPermissionGranted(context: Context) =
        (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_SETTINGS
        ) == PackageManager.PERMISSION_GRANTED)


    fun hasWriteSettingsPermissions(context: Context): Boolean{
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            false
        else Settings.System.canWrite(context)
    }
}