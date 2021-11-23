package com.farhazulmullick.videoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.farhazulmullick.videoplayer.databinding.ActivityExoplayerBinding
import com.farhazulmullick.viewmodel.VideoViewModel
import com.farhazulmullick.viewmodel.VideoViewModelFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer

class ExoplayerActivity : AppCompatActivity() {
    companion object{
        const val TAG = "ExoplayerActivity"
    }
    private  var _binding : ActivityExoplayerBinding? = null
    private val binding get() = _binding!!
    private  var player: SimpleExoPlayer? = null
    private lateinit var viewModel: VideoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityExoplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onUpBackbuttonClicked()

        val factory = VideoViewModelFactory.getInstance(application)
        viewModel = ViewModelProvider(this, factory).get(VideoViewModel::class.java)

        binding.imgPlayPause.setOnClickListener {
            if (player?.isPlaying == true){
                pauseMedia()
            }
            else{
                playMedia()
            }
        }

        binding.exoNext.setOnClickListener {
            viewModel.playNextVideo()
        }

        binding.exoPrev.setOnClickListener {
            viewModel.playPrevVideo()
        }
        viewModel.position.observe(this, Observer {
            Log.d(TAG, "position $it")
            player?.release()
            createPlayer(it)
        })
    }

    private fun createPlayer(position: Int){
        player = SimpleExoPlayer.Builder(this).build()
        binding.exoPlayerView.player = player
        //val videoUri = intent.getStringExtra("videoUri")
        val videoUri = viewModel.videoList.value?.get(position)?.videoPath
        val videoTitle = intent.getStringExtra("videoTitle")
        try{
            val mediaItem = MediaItem.fromUri(videoUri!!)
            //val mediaItem = MediaItem.fromUri(videoUri!!)
            binding.tvVideoTitle.text = videoTitle.toString()
            binding.tvVideoTitle.isSelected = true
            player?.setMediaItem(mediaItem)
            player?.prepare()
            playMedia()
        }
        catch (e: Exception){
            throw e
        }
    }

    private fun playMedia(){
        player?.let {player ->
            player.play()
            binding.imgPlayPause.setImageResource(R.drawable.ic_exo_icon_pause)
        }
    }

    private fun pauseMedia(){
        player?.let {player ->
            player.pause()
            binding.imgPlayPause.setImageResource(R.drawable.ic_exo_icon_play)
        }
    }

    private fun onUpBackbuttonClicked(){
        binding.btnVideoBack.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseMedia()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
        _binding = null
    }
}