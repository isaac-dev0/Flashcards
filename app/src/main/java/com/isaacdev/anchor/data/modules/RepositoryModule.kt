package com.isaacdev.anchor.data.modules

import com.isaacdev.anchor.data.repositories.AuthRepository
import com.isaacdev.anchor.data.repositories.DeckRepository
import com.isaacdev.anchor.data.repositories.FlashcardRepository
import com.isaacdev.anchor.data.repositories.implementations.AuthRepositoryImpl
import com.isaacdev.anchor.data.repositories.implementations.DeckRepositoryImpl
import com.isaacdev.anchor.data.repositories.implementations.FlashcardRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Dagger Hilt module that provides repository instances.
 * This module is installed in the [SingletonComponent], meaning the provided repositories
 * will have a singleton scope and will be available throughout the application's lifecycle.
 * It uses `@Binds` annotations to tell Hilt which implementation to use when an interface
 * is requested.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindDeckRepository(deckRepositoryImpl: DeckRepositoryImpl): DeckRepository

    @Binds
    abstract fun bindFlashcardRepository(flashcardRepositoryImpl: FlashcardRepositoryImpl): FlashcardRepository

    @Binds
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
}