package com.farhazulmullick.videoplayer.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farhazulmullick.adapter.VideoFragmentAdapter
import com.farhazulmullick.utils.PermissionType
import com.farhazulmullick.utils.checkForRequiredPermissions
import com.farhazulmullick.utils.toast
import com.farhazulmullick.utils.toastS
import com.farhazulmullick.videoplayer.databinding.FragmentAllVideosBinding
import com.farhazulmullick.viewmodel.VideoViewModel

class VideosFragment : Fragment() {
    private var _binding: FragmentAllVideosBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: VideoViewModel
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var videoAdapter: VideoFragmentAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = ViewModelProvider(requireActivity()).get(VideoViewModel::class.java)
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
            { result ->
                var allGranted = true
                result.forEach { isGranted ->
                    allGranted = allGranted and isGranted.value
                }

                if (allGranted) {
                    viewModel.fetchAllVideos()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAllVideosBinding.inflate(layoutInflater)
        binding.viewModel = viewModel

        setUpRecyclerView()
        viewModel.videoList.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                videoAdapter.setVideos(it)
            }
        })
        return binding.root
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

    override fun onStart() {
        super.onStart()

        activity?.checkForRequiredPermissions(listOf(PermissionType.STORAGE),
            onGranted = {
                viewModel.fetchAllVideos()
            },
            onDenied = {
                toastS("Please Grant media permissions to access files")
            }
        )
    }

    companion object {
        const val TAG = "VideosFragment"
    }
}