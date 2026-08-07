package br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps

sealed interface CreateMapOutcome {
    data object Success : CreateMapOutcome

    data class Failure(
        val error: CreateMapError,
    ) : CreateMapOutcome
}
