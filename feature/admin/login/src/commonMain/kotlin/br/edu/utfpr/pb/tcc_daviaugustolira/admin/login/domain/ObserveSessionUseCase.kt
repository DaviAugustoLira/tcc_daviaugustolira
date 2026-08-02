package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.domain

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionState
import kotlinx.coroutines.flow.Flow

class ObserveSessionUseCase(
    private val repository: AdminSessionRepository,
) {
    operator fun invoke(): Flow<AdminSessionState> = repository.observeSession()
}
