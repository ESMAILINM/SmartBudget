package edu.ucne.smartbudget.domain.usecase.ingresosusecase

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.repository.IngresoRepository
import javax.inject.Inject

class DeleteIngresoUseCase @Inject constructor(
    private val ingresoRepository: IngresoRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit>
    = ingresoRepository.deleteIngreso(id)
}