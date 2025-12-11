package edu.ucne.smartbudget.repository

import edu.ucne.smartbudget.data.local.dao.CategoriaDao
import edu.ucne.smartbudget.data.mapper.toEntity
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.remote.dto.categoriasdto.CategoriaResponse
import edu.ucne.smartbudget.data.remote.remotedatasource.CategoriasRemoteDataSource
import edu.ucne.smartbudget.data.remote.repository.CategoriasRepositoryImpl
import edu.ucne.smartbudget.domain.model.Categorias
import io.mockk.*
import junit.framework.Assert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriasRepositoryImplTest {

    private val dao = mockk<CategoriaDao>(relaxed = true)
    private val remote = mockk<CategoriasRemoteDataSource>()
    private lateinit var repository: CategoriasRepositoryImpl
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = CategoriasRepositoryImpl(dao, remote)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun insertCategoria_createsPendingLocal() = runTest {
        coEvery { dao.upsertCategoria(any()) } just Runs

        val cat = Categorias(
            categoriaId = "local1",
            remoteId = null,
            nombre = "Comida",
            tipoId = 1,
            isPendingCreate = false
        )

        val result = repository.insertCategoria(cat)
        assertTrue(result is Resource.Success)
        assertTrue(result.data!!.isPendingCreate)
        coVerify { dao.upsertCategoria(any()) }
    }

    @Test
    fun upsertCategoria_marksPendingCreateWhenNoRemoteId() = runTest {
        coEvery { dao.upsertCategoria(any()) } just Runs

        val cat = Categorias(
            categoriaId = "1",
            remoteId = null,
            nombre = "Test",
            tipoId = 1
        )

        val result = repository.upsertCategoria(cat)
        assertTrue(result is Resource.Success)
        coVerify { dao.upsertCategoria(withArg { assertTrue(it.isPendingCreate) }) }
    }

    @Test
    fun upsertCategoria_marksPendingUpdateWhenRemoteExists() = runTest {
        coEvery { dao.upsertCategoria(any()) } just Runs

        val cat = Categorias(
            categoriaId = "1",
            remoteId = 10,
            nombre = "Test",
            tipoId = 1
        )

        repository.upsertCategoria(cat)
        coVerify { dao.upsertCategoria(withArg { assertTrue(it.isPendingUpdate) }) }
    }

    @Test
    fun deleteCategoria_deletesLocalWhenNoRemoteId() = runTest {
        val entity = Categorias(
            categoriaId = "1",
            remoteId = null,
            nombre = "Test",
            tipoId = 1
        ).toEntity()

        coEvery { dao.getCategoria("1") } returns entity
        coEvery { dao.deleteCategoria("1") } just Runs

        val result = repository.deleteCategoria("1")
        assertTrue(result is Resource.Success)
        coVerify { dao.deleteCategoria("1") }
    }

    @Test
    fun deleteCategoria_marksPendingDeleteWhenRemoteExists() = runTest {
        val entity = Categorias(
            categoriaId = "1",
            remoteId = 5,
            nombre = "Test",
            tipoId = 1
        ).toEntity()

        coEvery { dao.getCategoria("1") } returns entity
        coEvery { dao.upsertCategoria(any()) } just Runs

        val result = repository.deleteCategoria("1")
        assertTrue(result is Resource.Success)
        coVerify { dao.upsertCategoria(withArg { assertTrue(it.isPendingDelete) }) }
    }

    @Test
    fun postPendingCategorias_syncsNewRemoteAndUpdatesLocal() = runTest {
        val localEntity = Categorias(
            categoriaId = "local",
            remoteId = null,
            nombre = "Test",
            tipoId = 1,
            isPendingCreate = true
        ).toEntity()

        val remoteResponse = CategoriaResponse(
            categoriaId = 100,
            nombre = "Test",
            tipoId = 1,
            tipoNombre = "Ingreso"
        )

        coEvery { dao.getPendingCreateCategorias() } returns listOf(localEntity)
        coEvery { remote.createCategoria(any()) } returns Resource.Success(remoteResponse)
        coEvery { dao.upsertCategoria(any()) } just Runs

        val result = repository.postPendingCategorias()
        assertTrue(result is Resource.Success)
        coVerify { dao.upsertCategoria(withArg { assertFalse(it.isPendingCreate) }) }
    }

    @Test
    fun postPendingUpdates_syncsRemoteAndClearsPendingUpdate() = runTest {
        val localEntity = Categorias(
            categoriaId = "1",
            remoteId = 10,
            nombre = "Test",
            tipoId = 1,
            isPendingUpdate = true
        ).toEntity()

        coEvery { dao.getPendingUpdate() } returns listOf(localEntity)
        coEvery { remote.updateCategoria(any(), any()) } returns Resource.Success(Unit)
        coEvery { dao.upsertCategoria(any()) } just Runs

        val result = repository.postPendingUpdates()
        assertTrue(result is Resource.Success)
        coVerify { dao.upsertCategoria(withArg { assertFalse(it.isPendingUpdate) }) }
    }

    @Test
    fun postPendingDeletes_deletesLocalWhenNoRemoteId() = runTest {
        val localEntity = Categorias(
            categoriaId = "1",
            remoteId = null,
            nombre = "DeleteMe",
            tipoId = 1,
            isPendingDelete = true
        ).toEntity()

        coEvery { dao.getPendingDelete() } returns listOf(localEntity)
        coEvery { dao.deleteCategoria("1") } just Runs

        repository.postPendingDeletes()
        coVerify { dao.deleteCategoria("1") }
    }

    @Test
    fun postPendingDeletes_deletesLocalOnRemoteSuccess() = runTest {
        val localEntity = Categorias(
            categoriaId = "1",
            remoteId = 50,
            nombre = "DeleteMe",
            tipoId = 1,
            isPendingDelete = true
        ).toEntity()

        coEvery { dao.getPendingDelete() } returns listOf(localEntity)
        coEvery { remote.deleteCategoria(50) } returns Resource.Success(Unit)
        coEvery { dao.deleteCategoria("1") } just Runs

        repository.postPendingDeletes()
        coVerify { dao.deleteCategoria("1") }
    }

    @Test
    fun getCategorias_filtersPendingDelete() = runTest {
        val list = listOf(
            Categorias("1", remoteId = 1, nombre = "A", tipoId = 1, isPendingDelete = false).toEntity(),
            Categorias("2", remoteId = 2, nombre = "B", tipoId = 1, isPendingDelete = true).toEntity(),
        )
        every { dao.observeCategorias() } returns flowOf(list)

        val result = repository.getCategorias().first()
        assertEquals(1, result.size)
        assertEquals("1", result.first().categoriaId)
    }

    @Test
    fun getCategoria_returnsLocalWhenExists() = runTest {
        val entity = Categorias(
            categoriaId = "1",
            remoteId = 1,
            nombre = "X",
            tipoId = 1
        ).toEntity()

        coEvery { dao.getCategoria("1") } returns entity

        val result = repository.getCategoria("1")
        assertTrue(result is Resource.Success)
        assertEquals("X", result.data?.nombre)
    }

    @Test
    fun getCategoria_fetchesRemoteWhenLocalMissing() = runTest {
        coEvery { dao.getCategoria("10") } returns null

        val remoteResponse = CategoriaResponse(
            categoriaId = 10,
            nombre = "Remote",
            tipoId = 1,
            tipoNombre = "Ingreso"
        )

        coEvery { remote.getCategoria(10) } returns Resource.Success(remoteResponse)
        coEvery { dao.upsertCategoria(any()) } just Runs

        val result = repository.getCategoria("10")

        assertTrue(result is Resource.Success)
        assertEquals("Remote", result.data?.nombre)
    }
}
