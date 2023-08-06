package com.farhazulmullick.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.farhazulmullick.permissionmanager.PermissionsActivity
import com.farhazulmullick.permissionmanager.PermissionsActivity.Companion.startPermissionsActivity
import com.farhazulmullick.permissionmanager.PermissionsCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PermissionUtils {
    fun isStoragePermissionGranted(context: Context?): Boolean {
        return if (context == null) {
            false
        } else {
            (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED)
        }
    }


    fun isSettingsPermissionGranted(context: Context) =
        (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_SETTINGS
        ) == PackageManager.PERMISSION_GRANTED)


    fun hasWriteSettingsPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            false
        else Settings.System.canWrite(context)
    }

}

fun FragmentActivity.checkForRequiredPermissions(
    permissionTypes: List<PermissionType>,
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    lifecycleScope.launch(Dispatchers.Main.immediate) {
        startPermissionsActivity(permissionTypes,
            object : PermissionsCallback {
                override fun onGranted() {
                    onGranted()
                }

                override fun onDenied() {
                    onDenied()
                }
            })
    }
}


enum class PermissionType {
    STORAGE,
    CAMERA,
    LOCATION,
    MICROPHONE,
    WRITE_SETTINGS,
    NOTIFICATION
}