package com.practice.trialapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

// network / disk work (firestore calls, connectivity check)
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

// cpu work (mapping, sorting, totals)
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

// Dispatchers are injected instead of hardcoding Dispatchers.IO everywhere,
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
