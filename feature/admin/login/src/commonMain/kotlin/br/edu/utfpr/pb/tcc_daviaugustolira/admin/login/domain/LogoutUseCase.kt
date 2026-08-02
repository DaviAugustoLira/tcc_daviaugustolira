package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionRepository

class LogoutUseCase(
    private val repository: AdminSessionRepository,
) {
    suspend operator fun invoke() {
        repository.logout()
    }
}
