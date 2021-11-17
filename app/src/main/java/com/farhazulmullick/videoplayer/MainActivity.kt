package com.farhazulmullick.videoplayer

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.farhazulmullick.videoplayer.databinding.ActivityMainBinding
import com.farhazulmullick.videoplayer.fragment.AllVideosFragment
import com.farhazulmullick.videoplayer.fragment.FoldersFragment

class MainActivity : AppCompatActivity() {
    companion object {
        const val STORAGE_PERMISSION_REQ_CODE = 101
        const val TAG = "MainActivity"
    }

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // databinding
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_1)
        setContentView(binding.root)
        hostFragment(AllVideosFragment())

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        )
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUptoolbar()
        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.all_videos -> {
                    hostFragment(AllVideosFragment())
                }
                R.id.all_folders -> {
                    hostFragment(FoldersFragment())
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun setUptoolbar() {
        binding.toolBar.setNavigationOnClickListener {
            binding.apply {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START)
                }else{
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (isStoragePermissionGranted()) {

        } else {
            Log.d(TAG, "Storage Permission Requested")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQ_CODE
            )
        }
    }

    private fun isStoragePermissionGranted() =
        (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_REQ_CODE -> {
                grantResults.forEach {
                    if (it != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermission() -> Storage Permission not Granted")
                        return
                    }
                }
                Log.d(TAG, "onRequestPermission() -> Storage Permission Granted")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}