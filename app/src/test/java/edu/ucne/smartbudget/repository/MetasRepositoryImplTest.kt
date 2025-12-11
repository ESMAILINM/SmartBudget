package edu.ucne.smartbudget.data.remote.repository

import edu.ucne.smartbudget.data.local.dao.ImagenesDao
import edu.ucne.smartbudget.data.local.dao.MetasDao
import edu.ucne.smartbudget.data.local.dao.UsuarioDao
import edu.ucne.smartbudget.data.local.entities.MetasEntity
import edu.ucne.smartbudget.data.local.entities.ImagenesEntity
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.remote.remotedatasource.MetasRemoteDataSource
import edu.ucne.smartbudget.domain.model.Metas
import edu.ucne.smartbudget.domain.model.Imagenes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class MetasRepositoryImplTest {

    private lateinit var repository: MetasRepositoryImpl

    private val metasDao = mock(MetasDao::class.java)
    private val imgsDao = mock(ImagenesDao::class.java)
    private val remote = mock(MetasRemoteDataSource::class.java)
    private val usuarioDao = mock(UsuarioDao::class.java)

    @Before
    fun setup() {
        repository = MetasRepositoryImpl(
            local = metasDao,
            imagenesDao = imgsDao,
            remote = remote,
            usuarioDao = usuarioDao
        )
    }

    @Test
    fun insertMeta_marksPendingCreate() = runTest {
        val meta = Metas(
            metaId = "m1",
            remoteId = null,
            nombre = "Meta",
            contribucionMensual = 100.0,
            monto = 1200.0,
            fecha = "2025-01-01",
            emoji = "🔥",
            usuarioId = "1",
            imagenes = listOf(
                Imagenes(imagenId = "i1", url = "local.jpg", metaId = "m1")
            )
        )

        val result = repository.insertMeta(meta)

        assertTrue(result is Resource.Success)
        assertTrue((result as Resource.Success).data!!.isPendingCreate)
    }

    @Test
    fun getMeta_returnsLocal() = runTest {
        val metaEntity = MetasEntity(
            metaId = "m1",
            remoteId = 5,
            nombre = "Meta",
            contribucionMensual = 100.0,
            monto = 1200.0,
            fecha = "2025-01-01",
            emoji = "🔥",
            usuarioId = "1",
            isPendingCreate = false,
            isPendingUpdate = false,
            isPendingDelete = false
        )

        val img = ImagenesEntity(
            imagenId = "i1",
            url = "x",
            metaId = "m1",
            remoteId = null,
            localUrl = "",
            isPendingCreate = false,
            isPendingUpdate = false,
            isPendingDelete = false
        )

        `when`(metasDao.getMeta("m1")).thenReturn(metaEntity)
        `when`(imgsDao.getImagesByMeta("m1")).thenReturn(listOf(img))

        val result = repository.getMeta("m1")

        assertTrue(result is Resource.Success)
        assertEquals("m1", (result as Resource.Success).data?.metaId)
    }

    @Test
    fun getMetas_returnsFlowList() = runTest {
        val entity = MetasEntity(
            metaId = "m1",
            remoteId = 3,
            nombre = "Meta",
            contribucionMensual = 200.0,
            monto = 3000.0,
            fecha = "2025-02-01",
            emoji = null,
            usuarioId = "1",
            isPendingCreate = false,
            isPendingUpdate = false,
            isPendingDelete = false
        )

        `when`(metasDao.observeMetasByUsuario("1")).thenReturn(flowOf(listOf(entity)))
        `when`(imgsDao.getImagesByMeta("m1")).thenReturn(emptyList())

        val result = repository.getMetas("1").first()

        assertEquals(1, result.size)
        assertEquals("m1", result.first().metaId)
    }

    @Test
    fun updateMeta_setsPendingUpdate() = runTest {
        val meta = Metas(
            metaId = "m1",
            remoteId = 2,
            nombre = "Meta",
            contribucionMensual = 150.0,
            monto = 2000.0,
            fecha = "2025-03-01",
            emoji = "💰",
            usuarioId = "1",
            imagenes = emptyList()
        )

        val result = repository.updateMeta(meta)

        assertTrue(result is Resource.Success)
    }

    @Test
    fun deleteMeta_returnsSuccess() = runTest {
        val entity = MetasEntity(
            metaId = "m1",
            remoteId = 10,
            nombre = "Meta",
            contribucionMensual = 80.0,
            monto = 1000.0,
            fecha = "2025-04-01",
            emoji = null,
            usuarioId = "1",
            isPendingCreate = false,
            isPendingUpdate = false,
            isPendingDelete = false
        )

        `when`(metasDao.getMeta("m1")).thenReturn(entity)

        val result = repository.deleteMeta("m1")

        assertTrue(result is Resource.Success)
    }
}
