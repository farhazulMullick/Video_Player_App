package com.farhazulmullick.feature.allvideos.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.farhazulmullick.core_ui.utils.PermissionType
import com.farhazulmullick.core_ui.utils.checkForRequiredPermissions
import com.farhazulmullick.feature.allvideos.adapter.VideoFragmentAdapter
import com.farhazulmullick.feature.allvideos.modal.Video
import com.farhazulmullick.feature.allvideos.ui.composable.VideosScreen
import com.farhazulmullick.feature.allvideos.viewmodel.VideoViewModel
import com.farhazulmullick.utils.VIDEO_MODEL
import com.farhazulmullick.utils.toastS
import com.farhazulmullick.videoplayer.ExoplayerActivity
import com.farhazulmullick.videoplayer.databinding.FragmentAllVideosBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideosFragment : Fragment() {
    private var _binding: FragmentAllVideosBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<VideoViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return if (context!=null) {
            activity?.checkForRequiredPermissions(listOf(PermissionType.STORAGE),
                onGranted = {
                    viewModel.fetchAllVideos(context = context!!)
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
                            onVideoItemClicked = {videoItem: Video->
                                Intent(context, ExoplayerActivity::class.java).apply {
                                    putExtra(VIDEO_MODEL, videoItem)
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