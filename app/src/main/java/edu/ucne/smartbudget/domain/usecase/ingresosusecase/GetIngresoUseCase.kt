package edu.ucne.smartbudget.domain.usecase.ingresosusecase

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Ingresos
import edu.ucne.smartbudget.domain.repository.IngresoRepository
import javax.inject.Inject

class GetIngresoUseCase @Inject constructor(
    private val ingresosRepository: IngresoRepository,
) {
    suspend operator fun invoke(id: String): Resource<Ingresos?> {
        return ingresosRepository.getIngreso(id)
    }
}
