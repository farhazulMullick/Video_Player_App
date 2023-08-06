package com.farhazulmullick.videoplayer.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.farhazulmullick.adapter.FoldersAdapter
import com.farhazulmullick.permissionmanager.PermissionsActivity
import com.farhazulmullick.utils.PermissionType
import com.farhazulmullick.utils.checkForRequiredPermissions
import com.farhazulmullick.utils.toastS
import com.farhazulmullick.videoplayer.databinding.FragmentFoldersBinding
import com.farhazulmullick.viewmodel.VideoViewModel

class FoldersFragment : Fragment() {
    private var _binding : FragmentFoldersBinding? = null
    private val binding get() = _binding!!
    //permission launcher
    private lateinit var folderAdapter:FoldersAdapter

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

        activity?.checkForRequiredPermissions(listOf(PermissionType.STORAGE),
            onGranted = {
                viewModel.fetchAllfolders()
            },
            onDenied = {
                toastS("Please Grant media permissions to access files")
            }
        )
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