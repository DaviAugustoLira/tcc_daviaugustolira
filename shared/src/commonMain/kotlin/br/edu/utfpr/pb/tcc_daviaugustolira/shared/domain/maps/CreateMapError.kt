package br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps

sealed interface CreateMapError {
    data object EmptyName : CreateMapError

    data object EmptySvgUrl : CreateMapError

    data object InvalidScale : CreateMapError

    data object InvalidFloor : CreateMapError

    data object NoActiveSession : CreateMapError

    data object NetworkUnavailable : CreateMapError

    data class Unknown(
        val message: String?,
    ) : CreateMapError
}
