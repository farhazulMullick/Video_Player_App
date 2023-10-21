package com.farhazulmullick.di

import android.content.Context
import com.farhazulmullick.data.local.AppDatabase
import com.farhazulmullick.data.local.buildVideoPlayerDatabase
import com.farhazulmullick.data.local.recentwatch.dao.VideosDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return context.buildVideoPlayerDatabase()
    }

    @Provides
    fun provideVideoDao(
        appDatabase: AppDatabase
    ): VideosDao = appDatabase.getVideosDao()
}