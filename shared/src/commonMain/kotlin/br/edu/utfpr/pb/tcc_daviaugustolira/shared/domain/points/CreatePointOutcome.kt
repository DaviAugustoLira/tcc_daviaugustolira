package br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points

sealed interface CreatePointOutcome {
    data object Success : CreatePointOutcome

    data class Failure(
        val error: CreatePointError,
    ) : CreatePointOutcome
}
