package com.farhazulmullick.feature.allvideos.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farhazulmullick.feature.allvideos.adapter.VideoFragmentAdapter
import com.farhazulmullick.core_ui.utils.PermissionType
import com.farhazulmullick.core_ui.utils.checkForRequiredPermissions
import com.farhazulmullick.feature.allvideos.modal.Video
import com.farhazulmullick.feature.allvideos.ui.composable.ContinueWatch
import com.farhazulmullick.utils.toast
import com.farhazulmullick.utils.toastS
import com.farhazulmullick.videoplayer.databinding.FragmentAllVideosBinding
import com.farhazulmullick.feature.allvideos.viewmodel.VideoViewModel
import com.farhazulmullick.videoplayer.ExoplayerActivity

class VideosFragment : Fragment() {
    private var _binding: FragmentAllVideosBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: VideoViewModel
    private lateinit var videoAdapter: VideoFragmentAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = ViewModelProvider(requireActivity()).get(VideoViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAllVideosBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        viewModel.videoList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                videoAdapter.setVideos(it)
            }
        }

        activity?.checkForRequiredPermissions(listOf(PermissionType.STORAGE),
            onGranted = {
                viewModel.fetchAllVideos()
            },
            onDenied = {
                toastS("Please Grant media permissions to access files")
            }
        )

        binding.continueCpv.setContent {
            MaterialTheme {
                val videoList by viewModel.videoList.observeAsState()
                AnimatedVisibility(visible = videoList !=null && videoList!!.isNotEmpty()) {
                    ContinueWatch(videoList = videoList ?: emptyList()) {video: Video ->
                        context?.run {
                            Intent(this@run, ExoplayerActivity::class.java).apply {
                                this.putExtra("videoUri", video.videoPath)
                                this.putExtra("videoTitle", video.videoTitle)
                                ContextCompat.startActivity(this@run, this, null)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        videoAdapter = VideoFragmentAdapter(requireContext(), viewModel)
        binding.rvTotalVideos.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(requireContext())

            when (scrollState) {
                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    context?.toast("Scrolling")
                }
            }
        }
    }

    companion object {
        const val TAG = "VideosFragment"
    }
}