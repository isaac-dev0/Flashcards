package com.isaacdev.anchor.data.modules

import com.isaacdev.anchor.data.database.SupabaseClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides database-related dependencies.
 *
 * This module is installed in the [SingletonComponent], meaning that the provided
 * dependencies will have a singleton scope and will be available throughout the
 * application's lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): io.github.jan.supabase.SupabaseClient {
        return SupabaseClient.client
    }
}