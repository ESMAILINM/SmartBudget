package edu.ucne.smartbudget.domain.usecase.gastosusecase

import edu.ucne.smartbudget.domain.model.Gastos
import edu.ucne.smartbudget.domain.repository.GastosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveGastosUseCase @Inject constructor(
    private val gastosRepository: GastosRepository
) {
    operator fun invoke(usuarioId: String): Flow<List<Gastos>> =
        gastosRepository.getGastos(usuarioId)
}
