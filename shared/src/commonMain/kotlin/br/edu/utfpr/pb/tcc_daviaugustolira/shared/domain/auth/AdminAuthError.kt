package br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth

sealed interface AdminAuthError {
    data object EmptyFields : AdminAuthError

    data object InvalidCredentials : AdminAuthError

    data object NetworkUnavailable : AdminAuthError

    data class Unknown(
        val message: String?,
    ) : AdminAuthError
}
