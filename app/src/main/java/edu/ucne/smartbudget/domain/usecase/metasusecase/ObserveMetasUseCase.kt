package edu.ucne.smartbudget.domain.usecase.metasusecase

import edu.ucne.smartbudget.domain.model.Metas
import edu.ucne.smartbudget.domain.repository.MetasRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMetasUseCase @Inject constructor(
    private val metasRepository: MetasRepository
) {
    operator fun invoke(usuarioId: String): Flow<List<Metas>> {
        return metasRepository.getMetas(usuarioId)
    }
}
