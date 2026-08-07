package br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points

sealed interface CreatePointError {
    data object EmptyName : CreatePointError

    data object InvalidCoordinate : CreatePointError

    data object NoActiveSession : CreatePointError

    data object NetworkUnavailable : CreatePointError

    data class Unknown(
        val message: String?,
    ) : CreatePointError
}
