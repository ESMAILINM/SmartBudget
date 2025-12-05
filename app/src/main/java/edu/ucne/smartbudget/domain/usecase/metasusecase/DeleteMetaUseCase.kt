package edu.ucne.smartbudget.domain.usecase.metasusecase

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.repository.MetasRepository
import javax.inject.Inject

class DeleteMetaUseCase @Inject constructor(
    private val repo: MetasRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> = repo.deleteMeta(id)
}
