package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminAuthError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.LoginOutcome

class LoginUseCase(
    private val repository: AdminSessionRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): LoginOutcome {
        if (email.isBlank() || password.isBlank()) {
            return LoginOutcome.Failure(AdminAuthError.EmptyFields)
        }
        return repository.login(email, password)
    }
}
