package edu.ucne.smartbudget.domain.usecase.metasusecase

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Metas
import edu.ucne.smartbudget.domain.repository.MetasRepository
import javax.inject.Inject

class InsertMetaUseCase @Inject constructor(
    private val metaRepository: MetasRepository
) {
    suspend operator fun invoke(meta: Metas): Resource<Metas> {
        return metaRepository.insertMeta(meta)
    }
}
