package com.farhazulmullick.feature.folders.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.farhazulmullick.feature.folders.adapters.FoldersAdapter
import com.farhazulmullick.videoplayer.databinding.FragmentFoldersBinding
import com.farhazulmullick.feature.allvideos.viewmodel.VideoViewModel

class FoldersFragment : Fragment() {
    private var _binding : FragmentFoldersBinding? = null
    private val binding get() = _binding!!
    //permission launcher
    private lateinit var folderAdapter: FoldersAdapter

    // viewModel
    private val viewModel: VideoViewModel by activityViewModels()
    override fun onAttach(context: Context) {
        super.onAttach(context)
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

//        activity?.checkForRequiredPermissions(listOf(PermissionType.STORAGE),
//            onGranted = {
//
//            },
//            onDenied = {
//                toastS("Please Grant media permissions to access files")
//            }
//        )

        viewModel.fetchAllfolders()
    }


    private fun setUpRecyclerView() {
        folderAdapter = FoldersAdapter(requireContext())
        binding.rvFolder.apply {
            adapter = folderAdapter
        }
    }

    companion object {
    }
}