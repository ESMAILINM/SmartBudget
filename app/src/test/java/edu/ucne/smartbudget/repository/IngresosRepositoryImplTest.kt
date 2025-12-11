package edu.ucne.smartbudget.data.remote.repository

import edu.ucne.smartbudget.data.local.dao.CategoriaDao
import edu.ucne.smartbudget.data.local.dao.IngresoDao
import edu.ucne.smartbudget.data.local.dao.UsuarioDao
import edu.ucne.smartbudget.data.local.entities.IngresosEntity
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.remote.remotedatasource.IngresosRemoteDataSource
import edu.ucne.smartbudget.domain.model.Ingresos
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class IngresosRepositoryImplTest {

    private lateinit var repository: IngresosRepositoryImpl

    private val ingresoDao = mock(IngresoDao::class.java)
    private val remote = mock(IngresosRemoteDataSource::class.java)
    private val categoriaDao = mock(CategoriaDao::class.java)
    private val usuarioDao = mock(UsuarioDao::class.java)

    @Before
    fun setup() {
        repository = IngresosRepositoryImpl(
            localDataSource = ingresoDao,
            remoteDataSource = remote,
            categoriaDao = categoriaDao,
            usuarioDao = usuarioDao
        )
    }

    @Test
    fun insertIngreso_returnsPending() = runTest {
        val ingreso = Ingresos(
            monto = 100.0,
            fecha = "2025-01-01",
            descripcion = "test",
            categoriaId = "1",
            usuarioId = "1"
        )

        val result = repository.insertIngreso(ingreso)

        assertTrue(result is Resource.Success)
        assertTrue((result as Resource.Success).data!!.isPendingCreate)
    }

    @Test
    fun upsertIngreso_marksPendingCreateIfNoRemoteId() = runTest {
        val ingreso = Ingresos(
            monto = 50.0,
            fecha = "2025-01-01",
            descripcion = null,
            categoriaId = "1",
            usuarioId = "1",
            remoteId = null
        )

        val result = repository.upsertIngreso(ingreso)

        assertTrue(result is Resource.Success)
    }

    @Test
    fun deleteIngreso_removesLocalWhenNoRemoteId() = runTest {
        val entity = IngresosEntity(
            ingresoId = "1",
            remoteId = null,
            monto = 100.0,
            fecha = "2025-01-01",
            descripcion = "",
            categoriaId = "1",
            usuarioId = "1",
            isPendingCreate = false,
            isPendingUpdate = false,
            isPendingDelete = false
        )

        `when`(ingresoDao.getIngreso("1")).thenReturn(entity)

        val result = repository.deleteIngreso("1")

        assertTrue(result is Resource.Success)
    }

    @Test
    fun getIngresos_returnsFlowList() = runTest {
        val list = listOf(
            IngresosEntity(
                ingresoId = "1",
                remoteId = 22,
                monto = 120.0,
                fecha = "2025-01-01",
                descripcion = "OK",
                categoriaId = "1",
                usuarioId = "5",
                isPendingCreate = false,
                isPendingUpdate = false,
                isPendingDelete = false
            )
        )

        `when`(ingresoDao.observeIngresosByUsuario("5")).thenReturn(flowOf(list))

        val result = repository.getIngresos("5").first()

        assertEquals(1, result.size)
        assertEquals("1", result.first().ingresoId)
    }

    @Test
    fun getIngreso_localFound_returnsSuccess() = runTest {
        val entity = IngresosEntity(
            ingresoId = "1",
            remoteId = 33,
            monto = 200.0,
            fecha = "2025-01-01",
            descripcion = "",
            categoriaId = "1",
            usuarioId = "1",
            isPendingCreate = false,
            isPendingUpdate = false,
            isPendingDelete = false
        )

        `when`(ingresoDao.getIngreso("1")).thenReturn(entity)

        val result = repository.getIngreso("1")

        assertTrue(result is Resource.Success)
        assertEquals("1", (result as Resource.Success).data?.ingresoId)
    }
}
