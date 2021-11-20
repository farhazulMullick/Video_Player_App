package com.farhazulmullick.viewmodel

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.farhazulmullick.modal.Folder
import com.farhazulmullick.modal.Video
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception

class VideoViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "VideoViewModel"
    }

    private lateinit var collection: Uri
    val videoList = MutableLiveData<List<Video>>()
    val folderList = MutableLiveData<List<Folder>>()
    var totalVideos: String? = null

    private val projections = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Video.Media.DATE_ADDED,
        MediaStore.Video.Media.DATA,
        MediaStore.Video.Media.BUCKET_ID
    )

    init {
        Log.d(TAG, "viewModel -> ${this.hashCode()}")

    }

    fun fetchAllVideos(){
        collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        val sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC"
        try {
            val cursor: Cursor? = this.getApplication<Application>().applicationContext.contentResolver.query(
                collection,
                projections,
                null,
                null,
                sortOrder
            )

            val dataList = ArrayList<Video>()
            while (cursor?.moveToNext() == true) {
                val nameCol =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                val idCol = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                val durationCol =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                        .toLong()
                val sizeCol =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)).toLong()
                val dateAddedCol =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))
                val folderNameCol =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                val pathCol = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))

                val file = File(pathCol)
                if (file.exists()) {
                    dataList.add(
                        Video(
                            videoId = idCol,
                            videoUri = collection,
                            videoName = nameCol,
                            videoDuration = durationCol,
                            videoSize = sizeCol,
                            videoDateAdded = dateAddedCol,
                            videoFolderName = folderNameCol,
                            videoPath = pathCol
                        )
                    )
                }


            }
            Log.d(TAG, "fetchVideos() -> cursor ${cursor.hashCode()}")
            videoList.value = dataList
            totalVideos = "Total Videos: ${dataList.size}"
            cursor?.close()
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "${e.message}")
        }
    }

    fun fetchAllfolders(){

        val sortOrder = MediaStore.Video.Media.BUCKET_DISPLAY_NAME + " ASC"
        val cursor = this.getApplication<Application>().applicationContext.contentResolver.query(
            collection,
            projections,
            null,
            null,
            sortOrder
        )
        val folderSet = mutableSetOf<Folder>()
        while (cursor?.moveToNext() == true) {
            val folderIdCol = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))
            val folderNameCol = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
            val pathCol = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))

            try {
                val file = File(pathCol)
                if (file.exists()) {
                    folderSet.add(
                        Folder(
                            folderId = folderIdCol,
                            folderName = folderNameCol
                        )
                    )
                }
            }

            catch (e: Exception){
                Log.d(TAG, "${e.message}")
            }

            folderList.value = folderSet.toList()
        }
    }


}