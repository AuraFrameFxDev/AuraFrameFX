package dev.aurakai.auraframefx.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.security.KeystoreManager
import dev.aurakai.auraframefx.security.SecurityContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
public object SecurityModule {
    @Provides
    @Singleton
    public fun provideKeystoreManager(@ApplicationContext context: Context): KeystoreManager =
        KeystoreManager(context)

    @Provides
    @Singleton
    public fun provideSecurityContext(
        @ApplicationContext context: Context,
        keystoreManager: KeystoreManager,
    ): SecurityContext = SecurityContext(context, keystoreManager)
}