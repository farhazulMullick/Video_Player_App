package com.farhazulmullick.core_ui.permissionmanager

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.farhazulmullick.core_ui.utils.PermissionType

class PermissionsActivity : FragmentActivity() {

    private val permissionResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {permissionMap ->
            if (permissionMap.isEmpty()) {
                permissionsCallbacks.onGranted()
            }
            else {
                var allGranted = true
                permissionMap.forEach { permissions ->
                    allGranted = allGranted and permissions.value
                }

                if (allGranted) permissionsCallbacks.onGranted()
                else permissionsCallbacks.onDenied()
            }
            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        val permissions =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent?.getParcelableArrayListExtra(PERMISSIONS, PermissionType::class.java)
                    ?: emptyList()
            else intent?.getParcelableArrayListExtra(PERMISSIONS) ?: emptyList<PermissionType>()

        checkForRequiredPermissions(permissions)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "hashCode: ${this@PermissionsActivity.hashCode()}")
    }

    private fun generatePermissionStrArray(permissionTypes: List<PermissionType>): List<String> {
        val permissionsList = mutableListOf<String>()

        permissionTypes.forEach { permissionType ->
            when (permissionType) {
                PermissionType.STORAGE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionsList.addPermissions(
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_AUDIO,
                        )
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        permissionsList.addPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    } else {
                        permissionsList.addPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    }
                }

                PermissionType.NOTIFICATION -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionsList.addPermissions(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                PermissionType.WRITE_SETTINGS -> {
                    permissionsList.addPermissions(Manifest.permission.WRITE_SETTINGS)
                }

                PermissionType.CAMERA -> {
                    permissionsList.addPermissions(Manifest.permission.CAMERA)
                }

                else -> {}
            }
        }
        return permissionsList
    }

    private fun MutableList<String>.addPermissions(vararg permissions: String) =
        permissions.forEach { this.add(it) }

    private fun checkForRequiredPermissions(permissionTypes: List<PermissionType>) {

        val permissions = generatePermissionStrArray(permissionTypes)
        permissionResult.launch(permissions.toTypedArray())
    }

    override fun finish() {
        super.finish()
        // removing exit animations of activity.
        overridePendingTransition(0, 0)
    }

    companion object {
        val TAG = "PermissionsActivity"
        const val PERMISSIONS = "permissions"

        private lateinit var permissionTypes: List<PermissionType>
        private lateinit var permissionsCallbacks: PermissionsCallback

        var intent: Intent? = null

        fun Context.startPermissionsActivity(
            permissionTypes: List<PermissionType>,
            listener: PermissionsCallback
        ) {
            Companion.permissionTypes = permissionTypes
            permissionsCallbacks = listener

            if (intent == null) {
                intent = Intent(this, PermissionsActivity::class.java)
                    .apply { flags = Intent.FLAG_ACTIVITY_SINGLE_TOP }
            }
            intent!!.putParcelableArrayListExtra(PERMISSIONS, ArrayList(permissionTypes))

            startActivity(intent)
        }

    }
}