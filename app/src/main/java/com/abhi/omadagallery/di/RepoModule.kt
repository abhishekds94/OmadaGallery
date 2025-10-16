package com.abhi.omadagallery.di

import com.abhi.omadagallery.domain.repo.PhotoRepository
import com.abhi.omadagallery.domain.repo.PhotoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    abstract fun bindRepo(impl: PhotoRepositoryImpl): PhotoRepository
}
