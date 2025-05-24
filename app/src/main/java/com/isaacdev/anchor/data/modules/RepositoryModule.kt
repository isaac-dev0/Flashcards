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