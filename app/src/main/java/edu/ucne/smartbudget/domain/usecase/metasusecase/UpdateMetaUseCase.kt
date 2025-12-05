package edu.ucne.smartbudget.domain.usecase.metasusecase

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Metas
import edu.ucne.smartbudget.domain.repository.MetasRepository
import javax.inject.Inject

class UpdateMetaUseCase @Inject constructor(
    private val metasRepository: MetasRepository
) {
    suspend operator fun invoke(meta: Metas): Resource<Unit> =
        metasRepository.updateMeta(meta)
}
