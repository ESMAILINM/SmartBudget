package edu.ucne.smartbudget.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ucne.smartbudget.data.local.dao.CategoriaDao
import edu.ucne.smartbudget.data.local.dao.GastoDao
import edu.ucne.smartbudget.data.local.dao.ImagenesDao
import edu.ucne.smartbudget.data.local.dao.IngresoDao
import edu.ucne.smartbudget.data.local.dao.MetasDao
import edu.ucne.smartbudget.data.local.entities.UsuariosEntity
import edu.ucne.smartbudget.data.local.dao.UsuarioDao
import edu.ucne.smartbudget.data.local.entities.IngresosEntity
import edu.ucne.smartbudget.data.local.entities.CategoriasEntity
import edu.ucne.smartbudget.data.local.entities.GastosEntity
import edu.ucne.smartbudget.data.local.entities.MetasEntity
import edu.ucne.smartbudget.data.local.entities.ImagenesEntity



@Database(
    entities = [
        UsuariosEntity::class,
        IngresosEntity::class,
        CategoriasEntity::class,
        GastosEntity::class,
        MetasEntity::class,
        ImagenesEntity::class
    ],
    version = 18,
    exportSchema = false
)
abstract class SmartBudgetDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun ingresoDao(): IngresoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun gastoDao(): GastoDao
    abstract fun metasDao(): MetasDao
    abstract fun imagenesDao(): ImagenesDao
}