package com.farhazulmullick.application

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.farhazulmullick.core_ui.utils.checkForRequiredPermissions
import com.farhazulmullick.feature.allvideos.ui.fragments.VideosFragment
import com.farhazulmullick.feature.folders.ui.fragments.FoldersFragment
import com.farhazulmullick.utils.toastS
import com.farhazulmullick.videoplayer.R
import com.farhazulmullick.videoplayer.databinding.ActivityMainBinding
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        const val STORAGE_PERMISSION_REQ_CODE = 101
        const val TAG = "MainActivity"
    }

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var toggle: ActionBarDrawerToggle

    val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this@MainActivity) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // databinding
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        )
        toggle.syncState()

        setUptoolbar()
        binding.bottomNavView.setOnItemSelectedListener {
             when (it.itemId) {
                R.id.all_videos -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragment_container_layout) !is VideosFragment)
                    hostFragment(VideosFragment())
                }

                R.id.all_folders -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragment_container_layout) !is FoldersFragment)
                    hostFragment(FoldersFragment())
                }
            }
            return@setOnItemSelectedListener true
        }
        checkIfAnyUpdateIsAvailable()
        checkForRequiredPermissions(
            permissionTypes = listOf(com.farhazulmullick.core_ui.utils.PermissionType.NOTIFICATION),
            onGranted = {
                afterPermissions()
            },
            onDenied = {
                afterPermissions()
            })
    }

    private fun afterPermissions() {
        hostFragment(VideosFragment())
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        resumeIfUpdateInProgress()
    }

    private fun resumeIfUpdateInProgress() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdateFlow(appUpdateInfo)
            }
        }
    }

    private fun checkIfAnyUpdateIsAvailable() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->

            if ((appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE))
            ) {
                startUpdateFlow(appUpdateInfo)
            }
        }
    }

    private val appUpdateResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {}
    private fun startUpdateFlow(updateInfo: AppUpdateInfo) {
        appUpdateManager.registerListener(listener)
        appUpdateManager.startUpdateFlowForResult(
            updateInfo,
            appUpdateResultLauncher,
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
        )
    }

    private val listener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.INSTALLED -> {
                toastS(com.farhazulmullick.core_ui.R.string.app_updated)
                unregisterUpdateListeners()
            }
            InstallStatus.FAILED -> {
                toastS(com.farhazulmullick.core_ui.R.string.app_update_failed)
                Log.e(TAG, "InstallStateUpdateListener: installError->${state.installErrorCode()}")
                unregisterUpdateListeners()
            }
            InstallStatus.DOWNLOADING -> {}

            else -> {}
        }

    }

    private fun unregisterUpdateListeners() {
        appUpdateManager.unregisterListener(listener)
    }

    private fun setUptoolbar() {
        binding.toolBar.setNavigationOnClickListener {
            binding.apply {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }
    }

    private fun hostFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            replace(R.id.fragment_container_layout, fragment)
            disallowAddToBackStack()
            commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}