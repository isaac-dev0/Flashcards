package com.isaacdev.anchor.data.modules

import com.isaacdev.anchor.data.database.SupabaseClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): io.github.jan.supabase.SupabaseClient {
        return SupabaseClient.client
    }
}