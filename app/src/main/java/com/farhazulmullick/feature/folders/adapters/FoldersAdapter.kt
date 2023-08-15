package com.farhazulmullick.feature.folders.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.farhazulmullick.feature.folders.modals.Folder
import com.farhazulmullick.feature.folders.ui.activity.FolderDetailsActivity
import com.farhazulmullick.videoplayer.databinding.LayoutRowFoldersBinding

class FoldersAdapter(private val context: Context) :
    RecyclerView.Adapter<FoldersAdapter.FolderViewHolder>() {

    var folderList = emptyList<Folder>()

    inner class FolderViewHolder(val binding: LayoutRowFoldersBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FolderViewHolder {
        val binding: LayoutRowFoldersBinding =
            LayoutRowFoldersBinding.inflate(LayoutInflater.from(context), parent, false)

        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val currentData = folderList[position]
        holder.binding.apply {
            tvVideoTitle.text =  currentData.folderName
            container.setOnClickListener {
                Intent(context, FolderDetailsActivity::class.java).apply {
                    putExtra("folderName", currentData.folderName)
                    putExtra("folderId", currentData.folderId)
                    context.startActivity(this)
                }
            }
        }
    }

    override fun getItemCount(): Int = folderList.size
    fun setData(newData: List<Folder>) {
        folderList = newData
        notifyDataSetChanged()
    }

}