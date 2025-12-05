package edu.ucne.smartbudget.domain.usecase.categoriasusecase

import edu.ucne.smartbudget.data.remote.Resource
import edu.ucne.smartbudget.domain.model.Categorias
import edu.ucne.smartbudget.domain.repository.CategoriaRepository
import javax.inject.Inject


class InsertCategoriaUseCase @Inject constructor(
    private val categoriaRepository: CategoriaRepository
) {
    suspend operator fun invoke(categoria: Categorias): Resource<Categorias> =
        categoriaRepository.insertCategoria(categoria)
}