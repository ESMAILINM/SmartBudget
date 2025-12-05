package edu.ucne.smartbudget.domain.usecase.gastosusecase

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.repository.GastosRepository
import javax.inject.Inject

class DeleteGastoUseCase @Inject constructor(
    private val repo: GastosRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> =
        repo.deleteGasto(id)
}
