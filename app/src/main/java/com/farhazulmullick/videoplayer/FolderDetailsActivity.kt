package com.farhazulmullick.videoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.farhazulmullick.videoplayer.databinding.ActivityFolderDetailsBinding

class FolderDetailsActivity : AppCompatActivity() {
    private var _binding : ActivityFolderDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFolderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra("folderName")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}