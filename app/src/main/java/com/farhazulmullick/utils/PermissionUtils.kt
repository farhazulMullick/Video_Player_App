package com.farhazulmullick.utils

import android.os.Build
import android.os.Parcelable
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.farhazulmullick.permissionmanager.PermissionsActivity.Companion.startPermissionsActivity
import com.farhazulmullick.permissionmanager.PermissionsCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

fun FragmentActivity.hasWriteSettingsPermissions(): Boolean {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        false
    else Settings.System.canWrite(this)
}

fun FragmentActivity.checkForRequiredPermissions(
    permissionTypes: List<PermissionType>,
    onGranted: () -> Unit = {},
    onDenied: () -> Unit = {}
) {

    lifecycleScope.launch(Dispatchers.Main.immediate) {
        startPermissionsActivity(permissionTypes, object : PermissionsCallback {
            override fun onGranted() {
                onGranted()
            }

            override fun onDenied() {
                onDenied()
            }
        })
    }

}
@Parcelize
sealed class PermissionType: Parcelable {
    object STORAGE: PermissionType()
    object CAMERA: PermissionType()
    object WRITE_SETTINGS: PermissionType()
    object NOTIFICATION: PermissionType()
}