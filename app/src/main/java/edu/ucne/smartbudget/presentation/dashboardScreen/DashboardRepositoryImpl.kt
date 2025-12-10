package edu.ucne.smartbudget.presentation.dashboardScreen

import edu.ucne.smartbudget.data.local.dao.CategoriaDao
import edu.ucne.smartbudget.data.local.dao.GastoDao
import edu.ucne.smartbudget.data.local.dao.ImagenesDao
import edu.ucne.smartbudget.data.local.dao.IngresoDao
import edu.ucne.smartbudget.data.local.dao.MetasDao
import edu.ucne.smartbudget.data.mapper.toDomain
import edu.ucne.smartbudget.presentation.dashboardScreen.model.DashboardRawData
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest


class DashboardRepositoryImpl @Inject constructor(
    private val ingresosDao: IngresoDao,
    private val imagenesDao: ImagenesDao,
    private val gastosDao: GastoDao,
    private val metasDao: MetasDao,
    private val categoriasDao: CategoriaDao
) : DashboardRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getDashboardData(usuarioId: String): Flow<DashboardRawData> {

        val ingresosFlow = ingresosDao.observeIngresosByUsuario(usuarioId)
            .map { list -> list.filter { it.usuarioId == usuarioId }.map { it.toDomain() } }

        val gastosFlow = gastosDao.observeGastosByUsuario(usuarioId)
            .map { list -> list.filter { it.usuarioId == usuarioId }.map { it.toDomain() } }

        val metasFlow = metasDao.observeMetasByUsuario(usuarioId)
            .mapLatest { list ->
                list
                    .filter { it.usuarioId == usuarioId }
                    .map { metaEntity ->
                        val imagenesEntity = imagenesDao.getImagesByMeta(metaEntity.metaId)
                        val imagenesDomain = imagenesEntity.map { it.toDomain() }
                        metaEntity.toDomain(imagenesDomain)
                    }
            }

        val categoriasFlow = categoriasDao.observeCategorias()
            .map { list -> list.map { it.toDomain() } }

        return combine(
            ingresosFlow,
            gastosFlow,
            metasFlow,
            categoriasFlow
        ) { ingresos, gastos, metas, categorias ->
            DashboardRawData(
                ingresos = ingresos,
                gastos = gastos,
                metas = metas,
                categorias = categorias
            )
        }.distinctUntilChanged()
    }
}
