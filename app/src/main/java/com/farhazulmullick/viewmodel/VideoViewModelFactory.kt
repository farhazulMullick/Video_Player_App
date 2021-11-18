package com.farhazulmullick.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

@Suppress("UNCHECKED_CAST")
class VideoViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoViewModel::class.java))
            return VideoViewModel(application) as T
        else
            throw IllegalArgumentException("Unable to create viewmodel")
    }
}