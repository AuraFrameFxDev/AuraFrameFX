package com.example.app.di

import android.content.Context
import com.example.app.data.UserPreferences // Assuming path from previous creation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for providing UserPreferences.
 * TODO: Reported as unused declaration. Ensure Hilt is set up and this module is processed.
 */
@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    /**
     * Provides UserPreferences.
     * @param _context Application context. Parameter reported as unused.
     * @return A UserPreferences instance.
     * TODO: Reported as unused. Implement if UserPreferences is used.
     */
    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext _context: Context): UserPreferences? {
        // TODO: Parameter _context reported as unused (Hilt will provide it).
        // Example: return UserPreferences(_context)
        return null // Placeholder, as UserPreferences itself is a placeholder.
    }
}
