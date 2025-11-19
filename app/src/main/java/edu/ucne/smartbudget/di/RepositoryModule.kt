package edu.ucne.smartbudget.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.smartbudget.data.remote.repository.UsuarioRepositoryImpl
import edu.ucne.smartbudget.domain.repository.UsuarioRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        usuarioRepositoryImpl: UsuarioRepositoryImpl
    ): UsuarioRepository
}