package com.farhazulmullick.videoplayer

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.farhazulmullick.utils.PermissionUtils
import com.farhazulmullick.utils.toast
import com.farhazulmullick.videoplayer.databinding.ActivityExoplayerBinding
import com.farhazulmullick.videoplayer.databinding.LayoutMoreFeaturesMenuBinding
import com.farhazulmullick.viewmodel.VideoViewModel
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.exoplayer2.audio.AudioAttributes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*

class ExoplayerActivity : AppCompatActivity() {
    companion object {
        const val TAG = "ExoplayerActivity"
        private var isFullScreenModeOn = false
        private var isMediaControlLocked = false
        private var isSubtitleOn = false
    }

    private var _binding: ActivityExoplayerBinding? = null
    private val binding get() = _binding!!
    private var player: SimpleExoPlayer? = null
    private lateinit var viewModel: VideoViewModel
    private lateinit var trackSelector: DefaultTrackSelector
    private var audioAttributes: AudioAttributes? = null
    private lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityExoplayerBinding.inflate(layoutInflater)
        setTheme(R.style.exoplayerViewStyle)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(VideoViewModel::class.java)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        onUpBackbuttonClicked()
        setUpImmersiveMode()
        getScreenOrientation()
        setExoControlsVisibility()

        initializeBindings()
    }


    private fun initializeBindings() {
        // morefeatures
        binding.exoMoreFeatures.setOnClickListener {
            pauseMedia()
            val customDialogView = LayoutInflater.from(this)
                .inflate(R.layout.layout_more_features_menu, binding.rootViewGroup, false)
            val bindingMF = LayoutMoreFeaturesMenuBinding.bind(customDialogView)

            // Exoplayer moreFeatures Dialog
            val dialog = MaterialAlertDialogBuilder(this, R.style.audio_track_theme)
                .setView(bindingMF.root)
                .setOnCancelListener {
                    playMedia()
                    setUpImmersiveMode()
                }
                .create()

            dialog.show()

            // Audio track option
            bindingMF.exoAudioTrack.setOnClickListener {
                dialog.dismiss()
                pauseMedia()

                val audioTrack = mutableListOf<String>()
                player?.let {
                    for (i in 0 until it.currentTrackGroups.length) {
                        audioTrack.add(
                            Locale(
                                it.currentTrackGroups.get(i).getFormat(0).language.toString()
                            ).displayLanguage
                        )
                        Log.d(
                            TAG,
                            "Audio-track lang ${
                                it.currentTrackGroups.get(i).getFormat(0).language.toString()
                            }"
                        )
                        Log.d(TAG, "Audio-Track${audioTrack[i]}")
                    }
                }
                MaterialAlertDialogBuilder(this, R.style.audio_track_theme)
                    .setOnCancelListener {
                        playMedia()
                        setUpImmersiveMode()
                    }
                    .setTitle(R.string.select_language)
                    .setItems(audioTrack.toTypedArray()) { _, position ->
                        this.toast("${audioTrack[position]} selected")
                        trackSelector.setParameters(
                            trackSelector.buildUponParameters()
                                .setPreferredAudioLanguage(audioTrack[position])
                        )
                    }
                    .create()
                    .show()

            }

            // Exoplayer Subtitles
            bindingMF.exoSubtitle.setOnClickListener {
                dialog.dismiss()
                if (isSubtitleOn) {
                    // turn off subtitle
                    trackSelector.parameters = DefaultTrackSelector.ParametersBuilder(this)
                        .setRendererDisabled(C.TRACK_TYPE_VIDEO, true).build()
                    this.toast("Subtitle off")
                    isSubtitleOn = false
                } else {
                    this.toast("Turn on Subtitle")
                    trackSelector.parameters = DefaultTrackSelector.ParametersBuilder(this)
                        .setRendererDisabled(C.TRACK_TYPE_VIDEO, false).build()
                    isSubtitleOn = true
                }
                playMedia()
                setUpImmersiveMode()
            }

        }

        // Exoplayer play-pause
        binding.exoPlayPause.setOnClickListener {
            if (player?.isPlaying == true) {
                pauseMedia()
            } else {
                playMedia()
            }
        }

        // Exoplayer next
        binding.exoNext.setOnClickListener {
            viewModel.playNextVideo()
        }

        // Exoplayer previous
        binding.exoPrev.setOnClickListener {
            viewModel.playPrevVideo()
        }


        viewModel.position.observe(this, Observer {
            Log.d(TAG, "position $it")
            createPlayer(it)
        })

        // Exoplayer full_screen mode
        binding.btnFullScreen.setOnClickListener {
            if (!isFullScreenModeOn) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                binding.btnFullScreen.setImageResource(R.drawable.ic_exo_exit_fullscreen)
                isFullScreenModeOn = true
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                binding.btnFullScreen.setImageResource(R.drawable.ic_exo_enter_fullscreen)
                isFullScreenModeOn = false
            }

        }

        binding.exoVolumeSlider.apply {
            addOnChangeListener { slider, value, fromUser ->
                Log.d(TAG, "addOnChangeListener() -> ${value * 100}")
                player?.apply {
                    volume = value
                }
                alpha = 0.8f
            }
        }

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M)
            binding.exoBrightnessSlider.visibility = View.GONE

        binding.exoBrightnessSlider.apply {
            addOnChangeListener { _, value, _ ->
                Log.d(TAG, "addOnChangeListener() -> ${value * 100}")
                val screenBrightnessValue = value * 255
                alpha = 0.8f
                val canWriteSettings = PermissionUtils.hasWriteSettingsPermissions(this@ExoplayerActivity)

                if (canWriteSettings){
                    Settings.System
                        .putInt(
                            this@ExoplayerActivity.contentResolver,
                            Settings.System.SCREEN_BRIGHTNESS_MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                        )

                    Settings.System
                        .putInt(
                            this@ExoplayerActivity.contentResolver,
                            Settings.System.SCREEN_BRIGHTNESS,
                            screenBrightnessValue.toInt()
                        )

                    Log.d(TAG, "ScreenBrightness ${screenBrightnessValue}")
                }
                else{
                    Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply{
                        startActivity(this)
                    }
                }

            }

        }

    }

    private fun prepareControlLock() {
        binding.exoLockOpen.setOnClickListener {
            if (!isMediaControlLocked) {
                binding.exoPlayerView.useController = false
                isMediaControlLocked = true
                binding.exoLockOpen.visibility = View.GONE
                binding.exoLockClose.visibility = View.VISIBLE
            }
        }

        binding.exoLockClose.setOnClickListener {
            if (isMediaControlLocked) {
                binding.exoPlayerView.useController = true
                isMediaControlLocked = false
                binding.exoLockClose.visibility = View.GONE
                binding.exoLockOpen.visibility = View.VISIBLE
            }
        }
    }

    private fun getScreenOrientation() {
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
            this.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun setExoControlsVisibility() {
        lifecycleScope.launch(Dispatchers.Main.immediate) {
            while (this.isActive) {
                delay(16)
                if (binding.exoPlayerView.isControllerVisible) {
                    showControls()
                } else {
                    hideControls()
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main.immediate) {
            while (this.isActive) {
                delay(3000)
                if (!binding.exoPlayerView.isControllerVisible) {
                    binding.exoVolumeSlider.alpha = 0.0f
                    binding.exoBrightnessSlider.alpha = 0.0f
                }
            }
        }
    }

    private fun hideControls() {
        binding.apply {
            exoBottomContainer.visibility = View.INVISIBLE
            exoMediaContainer.visibility = View.INVISIBLE
            exoTopContainer.visibility = View.INVISIBLE
        }
    }

    private fun showControls() {
        binding.apply {
            exoBottomContainer.visibility = View.VISIBLE
            exoMediaContainer.visibility = View.VISIBLE
            exoTopContainer.visibility = View.VISIBLE
        }
    }

    private fun createPlayer(position: Int) {
        // release old player
        player?.release()

        // create instance of new player object
        audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MOVIE)
            .build()

        trackSelector = DefaultTrackSelector(this)
        player = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).setAudioAttributes(
            audioAttributes!!, true
        ).build()
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Log.d(TAG, "onPlaybackStateChanged() -> $playbackState")
                if (playbackState == Player.STATE_ENDED) {

                    viewModel.playNextVideo()
                }
            }
        })
        binding.exoPlayerView.player = player
        val videoUri = viewModel.videoList.value?.get(position)?.videoPath
        val videoTitle = intent.getStringExtra("videoTitle")
        try {
            val mediaItem = MediaItem.fromUri(videoUri!!)
            binding.tvVideoTitle.text = videoTitle.toString()
            binding.tvVideoTitle.isSelected = true
            player?.setMediaItem(mediaItem)
            player?.prepare()
            playMedia()
            seekForward()
            seekBackWord()
            prepareControlLock()
        } catch (e: Exception) {
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

    private fun playMedia() {
        player?.let { player ->
            player.play()
            binding.exoPlayPause.setImageResource(R.drawable.ic_exo_icon_pause)
        }
    }

    private fun pauseMedia() {
        player?.let { player ->
            player.pause()
            binding.exoPlayPause.setImageResource(R.drawable.ic_exo_icon_play)
        }
    }

    private fun onUpBackbuttonClicked() {
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