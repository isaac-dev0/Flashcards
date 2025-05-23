package com.isaacdev.anchor.domain.provider

import com.isaacdev.anchor.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object AuthProvider {
    @Provides
    fun provideAuthRepository(): AuthRepository = AuthRepository()
}