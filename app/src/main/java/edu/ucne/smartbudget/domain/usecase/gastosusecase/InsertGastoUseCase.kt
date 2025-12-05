package edu.ucne.smartbudget.domain.usecase.gastosusecase

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Gastos
import edu.ucne.smartbudget.domain.repository.GastosRepository
import javax.inject.Inject

class InsertGastoUseCase @Inject constructor(
    private val gastosRepository: GastosRepository
) {
    suspend operator fun invoke(gasto: Gastos): Resource<Gastos> =
        gastosRepository.insertGasto(gasto)
}
