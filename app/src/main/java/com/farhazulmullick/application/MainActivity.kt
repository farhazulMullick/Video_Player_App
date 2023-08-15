package com.farhazulmullick.application

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.farhazulmullick.core_ui.utils.checkForRequiredPermissions
import com.farhazulmullick.feature.allvideos.ui.fragments.VideosFragment
import com.farhazulmullick.feature.folders.ui.fragments.FoldersFragment
import com.farhazulmullick.videoplayer.R
import com.farhazulmullick.videoplayer.databinding.ActivityMainBinding

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
                    hostFragment(VideosFragment())
                }
                R.id.all_folders -> {
                    hostFragment(FoldersFragment())
                }
            }
            return@setOnItemSelectedListener true
        }
        checkForRequiredPermissions(permissionTypes = listOf(com.farhazulmullick.core_ui.utils.PermissionType.NOTIFICATION))
        hostFragment(VideosFragment())
    }

    override fun onStart() {
        super.onStart()
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