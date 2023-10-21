package com.farhazulmullick.data.local.recentwatch.di

import com.farhazulmullick.data.local.recentwatch.datasource.RecentWatchDataSource
import com.farhazulmullick.data.local.recentwatch.datasource.RecentWatchDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RecentWatchModule {

    @Binds
    abstract fun provideRecentWatchDataSource(
        recentWatchDataSourceImpl: RecentWatchDataSourceImpl
    ): RecentWatchDataSource
}