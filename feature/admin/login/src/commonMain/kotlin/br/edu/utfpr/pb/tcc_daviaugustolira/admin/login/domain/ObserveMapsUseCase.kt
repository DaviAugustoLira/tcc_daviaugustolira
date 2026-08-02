package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.IndoorMap
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.MapsRepository
import kotlinx.coroutines.flow.Flow

class ObserveMapsUseCase(
    private val repository: MapsRepository,
) {
    operator fun invoke(): Flow<List<IndoorMap>> = repository.observeMaps()
}
