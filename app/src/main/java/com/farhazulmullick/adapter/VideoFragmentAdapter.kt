package com.farhazulmullick.adapter

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farhazulmullick.modal.Video
import com.farhazulmullick.videoplayer.ExoplayerActivity
import com.farhazulmullick.videoplayer.R
import com.farhazulmullick.videoplayer.databinding.LayoutRowVideoPlayerBinding
import com.farhazulmullick.viewmodel.VideoViewModel

class VideoFragmentAdapter(val context: Context, val viewModel: VideoViewModel) : RecyclerView.Adapter<VideoFragmentAdapter.VideoViewHolder>() {
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
            videoTitle.text = currentVideo.videoTitle
            videoTitle.isSelected = true
            videoFolder.text = currentVideo.videoFolderName
            videoFolder.isSelected = true
            elapsedtime.text = DateUtils.formatElapsedTime(currentVideo.videoDuration/1000)

            Glide.with(context)
                .load(currentVideo.videoPath)
                .apply {
                    this.placeholder(R.mipmap.ic_launcher)
                }
                .into(videoThumbnail)

            container.setOnClickListener {
                viewModel.position.value = position
                Intent(context, ExoplayerActivity::class.java).apply {
                    this.putExtra("videoUri", currentVideo.videoPath)
                    this.putExtra("videoTitle", currentVideo.videoTitle)
                    ContextCompat.startActivity(context, this, null)
                }
            }
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