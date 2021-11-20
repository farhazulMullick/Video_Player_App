package com.farhazulmullick.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.farhazulmullick.modal.Folder
import com.farhazulmullick.videoplayer.FolderDetailsActivity
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
    ): FoldersAdapter.FolderViewHolder {
        val binding: LayoutRowFoldersBinding =
            LayoutRowFoldersBinding.inflate(LayoutInflater.from(context), parent, false)

        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoldersAdapter.FolderViewHolder, position: Int) {
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