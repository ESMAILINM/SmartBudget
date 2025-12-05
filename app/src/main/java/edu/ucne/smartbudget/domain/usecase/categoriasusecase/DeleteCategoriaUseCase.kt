package edu.ucne.smartbudget.domain.usecase.categoriasusecase

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.repository.CategoriaRepository
import javax.inject.Inject

class DeleteCategoriaUseCase @Inject constructor(
    private val categoriaRepository: CategoriaRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> =
        categoriaRepository.deleteCategoria(id)
}
