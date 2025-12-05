package edu.ucne.smartbudget.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.smartbudget.data.remote.repository.CategoriasRepositoryImpl
import edu.ucne.smartbudget.data.remote.repository.GastosRepositoryImpl
import edu.ucne.smartbudget.data.remote.repository.IngresosRepositoryImpl
import edu.ucne.smartbudget.data.remote.repository.MetasRepositoryImpl
import edu.ucne.smartbudget.data.remote.repository.UsuarioRepositoryImpl
import edu.ucne.smartbudget.domain.repository.CategoriaRepository
import edu.ucne.smartbudget.domain.repository.GastosRepository
import edu.ucne.smartbudget.domain.repository.IngresoRepository
import edu.ucne.smartbudget.domain.repository.MetasRepository
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

    @Binds
    @Singleton
    abstract fun bindIngresoRepository(
        ingresoRepositoryImpl: IngresosRepositoryImpl
    ): IngresoRepository

    @Binds
    @Singleton
    abstract fun bindCategoriaRepository(
        categoriasRepositoryImpl: CategoriasRepositoryImpl
    ): CategoriaRepository

    @Binds
    @Singleton
    abstract fun bindGastoRepository(
        gastosRepositoryImpl: GastosRepositoryImpl
    ): GastosRepository

    @Binds
    @Singleton
    abstract fun bindMetasRepository(
        metasRepositoryImpl: MetasRepositoryImpl
    ): MetasRepository
}
