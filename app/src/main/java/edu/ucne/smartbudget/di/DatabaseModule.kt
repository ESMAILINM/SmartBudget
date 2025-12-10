package edu.ucne.smartbudget.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.smartbudget.data.local.dao.CategoriaDao
import edu.ucne.smartbudget.data.local.dao.GastoDao
import edu.ucne.smartbudget.data.local.dao.ImagenesDao
import edu.ucne.smartbudget.data.local.dao.IngresoDao
import edu.ucne.smartbudget.data.local.dao.MetasDao
import edu.ucne.smartbudget.data.local.dao.UsuarioDao
import edu.ucne.smartbudget.data.local.database.SmartBudgetDatabase
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSmaertBudgetDb(@ApplicationContext appContext: Context): SmartBudgetDatabase {
        return Room.databaseBuilder(
            appContext,
            SmartBudgetDatabase::class.java,
            "smartBudgetDatabase.db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

        @Provides
        @Singleton
        fun provideUsuarioDao(db: SmartBudgetDatabase): UsuarioDao = db.usuarioDao()

        @Provides
        @Singleton
        fun provideIngresoDao(db: SmartBudgetDatabase): IngresoDao = db.ingresoDao()

        @Provides
        @Singleton
        fun provideCategoriaDao(db: SmartBudgetDatabase): CategoriaDao = db.categoriaDao()

        @Provides
        @Singleton
        fun provideGastoDao(db: SmartBudgetDatabase): GastoDao = db.gastoDao()

        @Provides
        @Singleton
        fun provideMetasDao(db: SmartBudgetDatabase): MetasDao = db.metasDao()

        @Provides
        @Singleton
        fun provideImagenesDao(db: SmartBudgetDatabase): ImagenesDao = db.imagenesDao()


    }

