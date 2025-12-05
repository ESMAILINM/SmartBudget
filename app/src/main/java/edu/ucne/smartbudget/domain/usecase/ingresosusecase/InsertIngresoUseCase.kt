package edu.ucne.smartbudget.domain.usecase.ingresosusecase

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Ingresos
import edu.ucne.smartbudget.domain.repository.IngresoRepository
import javax.inject.Inject

class InsertIngresoUseCase @Inject constructor(
    private val ingresoRepository: IngresoRepository
) {
    suspend operator fun invoke(ingreso: Ingresos): Resource<Ingresos> {
        return ingresoRepository.insertIngreso(ingreso)
    }
}
