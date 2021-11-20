package com.farhazulmullick.videoplayer.fragment

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.farhazulmullick.adapter.FoldersAdapter
import com.farhazulmullick.utils.PermissionUtils
import com.farhazulmullick.videoplayer.R
import com.farhazulmullick.videoplayer.databinding.FragmentFoldersBinding
import com.farhazulmullick.viewmodel.VideoViewModel
import com.farhazulmullick.viewmodel.VideoViewModelFactory

class FoldersFragment : Fragment() {
    private var _binding : FragmentFoldersBinding? = null
    private val binding get() = _binding!!
    //permission launcher
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var folderAdapter:FoldersAdapter

    // viewModel
    private lateinit var viewModel: VideoViewModel
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val factory = VideoViewModelFactory(activity?.application!!)
        viewModel = ViewModelProvider(requireActivity(), factory).get(VideoViewModel::class.java)
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        {result ->
            var allGranted = true
            result.forEach{isGranted->
                allGranted = allGranted and isGranted.value
            }

            if (allGranted){
                viewModel.fetchAllfolders()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFoldersBinding.inflate(layoutInflater)
        setUpRecyclerView()
        viewModel.folderList.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                folderAdapter.setData(it)
            }
        })
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val permissionList = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (PermissionUtils.isStoragePermissionGranted(requireContext())){
            // do work
            viewModel.fetchAllfolders()
        }
        else{
            //request permissions
            permissionLauncher.launch(permissionList)
        }
    }


    private fun setUpRecyclerView() {
        folderAdapter = FoldersAdapter(requireContext())
        binding.rvFolder.apply {
            adapter = folderAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    companion object {
    }
}