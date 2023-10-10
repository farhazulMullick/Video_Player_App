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
import androidx.compose.ui.platform.ComposeView
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
import com.farhazulmullick.feature.allvideos.ui.composable.VideosScreen
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
        return if (context!=null) {
            activity?.checkForRequiredPermissions(listOf(PermissionType.STORAGE),
                onGranted = {
                    viewModel.fetchAllVideos()
                },
                onDenied = {
                    toastS("Please Grant media permissions to access files")
                }
            )
            ComposeView(context = requireContext()).apply {
                setContent{
                    MaterialTheme {
                        VideosScreen(
                            viewModel = viewModel,
                            onVideoItemClicked = {
                                Intent(context, ExoplayerActivity::class.java).apply {
                                    this.putExtra("videoUri", it.videoPath)
                                    this.putExtra("videoTitle", it.videoTitle)
                                    ContextCompat.startActivity(context, this, null)
                                }
                            }
                        )
                    }
                }
            }
        } else null
    }

    companion object {
        const val TAG = "VideosFragment"
    }
}