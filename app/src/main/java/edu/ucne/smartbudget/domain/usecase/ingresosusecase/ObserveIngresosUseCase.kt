package edu.ucne.smartbudget.domain.usecase.ingresosusecase

import edu.ucne.smartbudget.domain.model.Ingresos
import edu.ucne.smartbudget.domain.repository.IngresoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIngresosUseCase @Inject constructor(
    private val ingresoRepository: IngresoRepository
) {
    operator fun invoke(usuarioId: String): Flow<List<Ingresos>> {
        return ingresoRepository.getIngresos(usuarioId)
    }
}