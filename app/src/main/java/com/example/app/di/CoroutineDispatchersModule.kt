package com.example.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

/**
 * Hilt Module for providing Coroutine Dispatchers.
 * TODO: Reported as unused declaration. Ensure Hilt is set up and this module is processed.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoroutineDispatchersModule {

    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class IoDispatcher

    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class DefaultDispatcher

    /**
     * Provides the IO Coroutine Dispatcher.
     * TODO: Reported as unused. Ensure this is injected where IO operations are performed.
     */
    @Provides
    @IoDispatcher
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides the Default Coroutine Dispatcher.
     * TODO: Reported as unused. Ensure this is injected for CPU-intensive tasks.
     */
    @Provides
    @DefaultDispatcher
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    // You could also provide Dispatchers.Main if needed, though it's often accessed directly.
    // @Provides
    // fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
