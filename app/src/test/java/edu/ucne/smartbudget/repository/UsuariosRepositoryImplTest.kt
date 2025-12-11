package edu.ucne.smartbudget.data.remote.repository

import edu.ucne.smartbudget.data.local.dao.UsuarioDao
import edu.ucne.smartbudget.data.mapper.toEntity
import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.data.remote.remotedatasource.UsuariosRemoteDataSource
import edu.ucne.smartbudget.domain.model.Usuarios
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsuariosRepositoryImplTest {

    private lateinit var repository: UsuarioRepositoryImpl
    private val local = mockk<UsuarioDao>(relaxed = true)
    private val remote = mockk<UsuariosRemoteDataSource>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = UsuarioRepositoryImpl(local, remote)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun insertUsuario_insertsPendingUser() = runTest {
        val usuario = Usuarios(
            usuarioId = "1",
            remoteId = null,
            userName = "Test",
            password = "1234"
        )

        coEvery { local.upsertUsuario(any()) } just Runs

        val result = repository.insertUsuario(usuario)

        assertTrue(result is Resource.Success)
        coVerify { local.upsertUsuario(any()) }
    }

    @Test
    fun deleteUsuario_localDeleteIfNotRemote() = runTest {
        val usuario = Usuarios(
            usuarioId = "1",
            remoteId = null,
            userName = "Test",
            password = "1234"
        )

        coEvery { local.getUsuario("1") } returns usuario.toEntity()
        coEvery { local.deleteUsuario("1") } just Runs

        val result = repository.deleteUsuario("1")

        assertTrue(result is Resource.Success)
        coVerify { local.deleteUsuario("1") }
    }

    @Test
    fun getUsuario_returnsLocalWhenExists() = runTest {
        val usuario = Usuarios(
            usuarioId = "1",
            remoteId = 10,
            userName = "User",
            password = "pass"
        )

        coEvery { local.getUsuario("1") } returns usuario.toEntity()

        val result = repository.getUsuario("1")

        assertTrue(result is Resource.Success)
        assertEquals("User", result.data?.userName)
    }

    @Test
    fun login_success() = runTest {
        val usuario = Usuarios(
            usuarioId = "1",
            userName = "Test",
            password = "1234",
            remoteId = null
        )

        coEvery { local.getUsuarioByUsername("Test") } returns usuario.toEntity()

        val result = repository.login("Test", "1234")

        assertTrue(result is Resource.Success)
    }

    @Test
    fun login_error() = runTest {
        coEvery { local.getUsuarioByUsername("Test") } returns null

        val result = repository.login("Test", "1234")

        assertTrue(result is Resource.Error)
    }
}
