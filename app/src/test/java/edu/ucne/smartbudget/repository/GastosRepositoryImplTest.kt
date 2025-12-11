package edu.ucne.smartbudget.data.remote.repository

import edu.ucne.smartbudget.data.local.dao.CategoriaDao
import edu.ucne.smartbudget.data.local.dao.GastoDao
import edu.ucne.smartbudget.data.local.dao.UsuarioDao
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.remote.remotedatasource.GastosRemoteDataSource
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
class GastosRepositoryImplTest {

    private lateinit var repository: GastosRepositoryImpl
    private val gastoDao = mock(GastoDao::class.java)
    private val usuarioDao = mock(UsuarioDao::class.java)
    private val categoriaDao = mock(CategoriaDao::class.java)
    private val remote = mock(GastosRemoteDataSource::class.java)

    @Before
    fun setup() {
        repository = GastosRepositoryImpl(
            dao = gastoDao,
            usuarioDao = usuarioDao,
            categoriaDao = categoriaDao,
            remote = remote
        )
    }

    @Test
    fun getGastos_returnsFlowList() = runTest {
        val list = listOf(
            edu.ucne.smartbudget.data.local.entities.GastosEntity(
                gastoId = "1",
                remoteId = null,
                monto = 100.0,
                fecha = "2025-01-01",
                descripcion = "Test",
                categoriaId = "1",
                usuarioId = "abc",
                isPendingCreate = false,
                isPendingUpdate = false,
                isPendingDelete = false
            )
        )

        `when`(gastoDao.observeGastosByUsuario("abc")).thenReturn(flowOf(list))

        val result = repository.getGastos("abc").first()

        assertEquals(1, result.size)
        assertEquals("1", result.first().gastoId)
    }

    @Test
    fun getGasto_returnsResourceSuccess() = runTest {
        val entity = edu.ucne.smartbudget.data.local.entities.GastosEntity(
            gastoId = "1",
            remoteId = 9,
            monto = 50.0,
            fecha = "2025-01-01",
            descripcion = null,
            categoriaId = "1",
            usuarioId = "abc",
            isPendingCreate = false,
            isPendingUpdate = false,
            isPendingDelete = false
        )

        `when`(gastoDao.getGasto("1")).thenReturn(entity)

        val res = repository.getGasto("1")

        assertTrue(res is Resource.Success)
        assertEquals("1", (res as Resource.Success).data?.gastoId)
    }

    @Test
    fun deleteGasto_localOnly_whenRemoteIdNull() = runTest {
        val entity = edu.ucne.smartbudget.data.local.entities.GastosEntity(
            gastoId = "1",
            remoteId = null,
            monto = 200.0,
            fecha = "2025-01-01",
            descripcion = null,
            categoriaId = "1",
            usuarioId = "abc",
            isPendingCreate = false,
            isPendingUpdate = false,
            isPendingDelete = false
        )

        `when`(gastoDao.getGasto("1")).thenReturn(entity)

        val result = repository.deleteGasto("1")

        assertTrue(result is Resource.Success)
    }
}
