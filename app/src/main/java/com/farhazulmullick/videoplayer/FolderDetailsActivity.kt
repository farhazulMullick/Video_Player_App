package com.farhazulmullick.videoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.farhazulmullick.feature.allvideos.adapter.VideoFragmentAdapter
import com.farhazulmullick.videoplayer.databinding.ActivityFolderDetailsBinding
import com.farhazulmullick.feature.allvideos.viewmodel.VideoViewModel

class FolderDetailsActivity : AppCompatActivity() {
    private var _binding : ActivityFolderDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var videoAdapter: VideoFragmentAdapter

    private lateinit var viewModel: VideoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFolderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val factory = VideoViewModelFactory.getInstance(application)
        viewModel = ViewModelProvider(this).get(VideoViewModel::class.java)
        binding.viewModel = viewModel
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra("folderName")


        setUpRecyclerView()
        viewModel.videoList.observe(this, Observer {
            if (it.isNotEmpty()){
                videoAdapter.setVideos(it)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val folderId = intent.getStringExtra("folderId")
        folderId?.let {
            viewModel.fetchVideosOfFolder(it)
        }
    }

    private fun setUpRecyclerView() {
        videoAdapter = VideoFragmentAdapter(this, viewModel)
        binding.rvTotalVideos.apply{
            layoutManager = LinearLayoutManager(this@FolderDetailsActivity)
            adapter = videoAdapter
        }
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