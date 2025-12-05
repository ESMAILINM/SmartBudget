package edu.ucne.smartbudget.domain.usecase.categoriasusecase

import edu.ucne.smartbudget.domain.model.Categorias
import edu.ucne.smartbudget.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCategoriasUseCase @Inject constructor(
    private val categoriaRepository: CategoriaRepository
) {
    operator fun invoke(): Flow<List<Categorias>> =
        categoriaRepository.getCategorias()
}