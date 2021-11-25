package com.farhazulmullick.videoplayer

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.farhazulmullick.videoplayer.databinding.ActivityExoplayerBinding
import com.farhazulmullick.videoplayer.databinding.LayoutMoreFeaturesMenuBinding
import com.farhazulmullick.viewmodel.VideoViewModel
import com.farhazulmullick.viewmodel.VideoViewModelFactory
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ExoplayerActivity : AppCompatActivity() {
    companion object{
        const val TAG = "ExoplayerActivity"
        private var isFullScreenModeOn = false
        private var isMediaControlLocked = false
    }
    private  var _binding : ActivityExoplayerBinding? = null
    private val binding get() = _binding!!
    private  var player: SimpleExoPlayer? = null
    private lateinit var viewModel: VideoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityExoplayerBinding.inflate(layoutInflater)
        setTheme(R.style.exoplayerViewStyle)
        setContentView(binding.root)

        onUpBackbuttonClicked()
        setUpImmersiveMode()
        getScreenOrientation()
        setExoControlsVisibility()

        val factory = VideoViewModelFactory.getInstance(application)
        viewModel = ViewModelProvider(this, factory).get(VideoViewModel::class.java)

        binding.exoPlayPause.setOnClickListener {
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
            createPlayer(it)
        })

        binding.btnFullScreen.setOnClickListener {
            if ( !isFullScreenModeOn){
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                binding.btnFullScreen.setImageResource(R.drawable.ic_exo_exit_fullscreen)
                isFullScreenModeOn = true
            }
            else{
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                binding.btnFullScreen.setImageResource(R.drawable.ic_exo_enter_fullscreen)
                isFullScreenModeOn = false
            }

        }

        // moreFeatures
        binding.exoMoreFeatures.setOnClickListener {
            pauseMedia()
            val customDialogView = LayoutInflater.from(this).
            inflate(R.layout.layout_more_features_menu, binding.rootViewGroup, false)

            val bindingMF = LayoutMoreFeaturesMenuBinding.bind(customDialogView)
            val dialog = MaterialAlertDialogBuilder(this, R.style.audio_track_theme)
                .setView(bindingMF.root)
                .setOnCancelListener {
                    playMedia()
                    setUpImmersiveMode()}
                .create()

            dialog.show()

            // Audio track option
            bindingMF.exoAudioTrack.setOnClickListener {
                dialog.dismiss()
                MaterialAlertDialogBuilder(this, R.style.audio_track_theme)
                    .setOnCancelListener { playMedia() }
                    .setTitle(R.string.select_language)
                    .create()
                    .show()
            }

        }

    }

    private fun prepareControlLock() {
        binding.exoLockOpen.setOnClickListener {
            if ( !isMediaControlLocked){
                binding.exoPlayerView.useController = false
                isMediaControlLocked = true
                binding.exoLockOpen.visibility = View.GONE
                binding.exoLockClose.visibility = View.VISIBLE
            }
        }

        binding.exoLockClose.setOnClickListener {
            if ( isMediaControlLocked){
                binding.exoPlayerView.useController = true
                isMediaControlLocked = false
                binding.exoLockClose.visibility = View.GONE
                binding.exoLockOpen.visibility = View.VISIBLE
            }
        }
    }

    private fun getScreenOrientation(){
        Log.d(TAG, "Orientation ${this.resources.configuration.orientation}")
    }

    override fun onStart() {
        super.onStart()
        setUpImmersiveMode()
        Log.d(TAG, "onStart()")
    }

    private fun setUpImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).apply {
            this.hide(WindowInsetsCompat.Type.systemBars())
            this.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun setExoControlsVisibility(){
        lifecycleScope.launch(Dispatchers.Main.immediate) {
            while (this.isActive){
                delay(16)
                if (binding.exoPlayerView.isControllerVisible){
                    showControls()
                }
                else{
                    hideControls()
                }
                //Log.d(TAG, "setVisibility() -> ${System.currentTimeMillis()/1000}")
            }
        }
    }

    private fun hideControls(){
        binding.apply {
            exoBottomContainer.visibility = View.INVISIBLE
            exoMediaContainer.visibility = View.INVISIBLE
            exoTopContainer.visibility = View.INVISIBLE
        }
    }

    private fun showControls(){
        binding.apply {
            exoBottomContainer.visibility = View.VISIBLE
            exoMediaContainer.visibility = View.VISIBLE
            exoTopContainer.visibility = View.VISIBLE
        }
    }

    private fun createPlayer(position: Int){
        // release old player
        player?.release()

        // create instance of new player object
        player = SimpleExoPlayer.Builder(this).build()
        player?.addListener(object: Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Log.d(TAG, "onPlaybackStateChanged() -> $playbackState")
                if (playbackState == Player.STATE_ENDED){

                    viewModel.playNextVideo()
                }
            }
        })
        binding.exoPlayerView.player = player
        val videoUri = viewModel.videoList.value?.get(position)?.videoPath
        val videoTitle = intent.getStringExtra("videoTitle")
        try{
            val mediaItem = MediaItem.fromUri(videoUri!!)
            binding.tvVideoTitle.text = videoTitle.toString()
            binding.tvVideoTitle.isSelected = true
            player?.setMediaItem(mediaItem)
            player?.prepare()
            playMedia()
            seekForward()
            seekBackWord()
            prepareControlLock()
        }
        catch (e: Exception){
            Log.d(TAG, "Sorry something went worng->  Exection: ${e.message}")
        }
    }

    private fun seekForward() {
        binding.exoSeekForward.setOnClickListener {
            player?.seekTo(player?.currentPosition!! + 10000)
        }
    }

    private fun seekBackWord() {
        binding.exoSeekBackword.setOnClickListener {
            player?.seekTo(player?.currentPosition!! - 10000)
        }
    }

    private fun playMedia(){
        player?.let {player ->
            player.play()
            binding.exoPlayPause.setImageResource(R.drawable.ic_exo_icon_pause)
        }
    }

    private fun pauseMedia(){
        player?.let {player ->
            player.pause()
            binding.exoPlayPause.setImageResource(R.drawable.ic_exo_icon_play)
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