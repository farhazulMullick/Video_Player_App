package com.farhazulmullick.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.farhazulmullick.modal.Video
import com.farhazulmullick.videoplayer.R
import com.farhazulmullick.videoplayer.databinding.LayoutRowVideoPlayerBinding

class VideoFragmentAdapter(context: Context) : RecyclerView.Adapter<VideoFragmentAdapter.VideoViewHolder>() {
    inner class VideoViewHolder(val binding: LayoutRowVideoPlayerBinding) :
        RecyclerView.ViewHolder(binding.root){

        }

    private var videoList = emptyList<Video>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            LayoutRowVideoPlayerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val currentVideo = videoList[position]
        holder.binding.apply {
            videoTitle.text = currentVideo.videoName
            videoTitle.isSelected = true
            videoFolder.text = currentVideo.videoFolderName
            videoFolder.isSelected = true
            elapsedtime.text = DateUtils.formatElapsedTime(currentVideo.videoDuration/1000)
        }
    }

    override fun getItemCount(): Int {
        return videoList.size;
    }

    fun setVideos(newData: List<Video>){
        videoList = newData
        notifyDataSetChanged()
    }
}