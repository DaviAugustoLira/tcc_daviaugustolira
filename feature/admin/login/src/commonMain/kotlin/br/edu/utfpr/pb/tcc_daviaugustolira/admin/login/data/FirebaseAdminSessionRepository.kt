package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminAuthError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSession
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminSessionState
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.LoginOutcome
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseException
import dev.gitlive.firebase.FirebaseNetworkException
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidUserException
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Única implementação concreta de [AdminSessionRepository] no projeto — feature/admin/login
 * é a dona da integração com Firebase Auth (ver CLAUDE.md secao 5); demais consumidores só
 * enxergam a interface via Koin.
 */
class FirebaseAdminSessionRepository(
    private val auth: FirebaseAuth = Firebase.auth,
) : AdminSessionRepository {
    override fun observeSession(): Flow<AdminSessionState> =
        auth.authStateChanged.map { user ->
            if (user == null) {
                AdminSessionState.Unauthenticated
            } else {
                AdminSessionState.Authenticated(
                    AdminSession(uid = user.uid, email = user.email.orEmpty()),
                )
            }
        }

    override suspend fun login(
        email: String,
        password: String,
    ): LoginOutcome =
        try {
            val user = auth.signInWithEmailAndPassword(email, password).user
            requireNotNull(user) { "Firebase Auth retornou sucesso sem usuário autenticado" }
            LoginOutcome.Success(AdminSession(uid = user.uid, email = user.email.orEmpty()))
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            LoginOutcome.Failure(AdminAuthError.InvalidCredentials)
        } catch (_: FirebaseAuthInvalidUserException) {
            LoginOutcome.Failure(AdminAuthError.InvalidCredentials)
        } catch (_: FirebaseNetworkException) {
            LoginOutcome.Failure(AdminAuthError.NetworkUnavailable)
        } catch (e: FirebaseAuthException) {
            LoginOutcome.Failure(AdminAuthError.Unknown(e.message))
        } catch (e: FirebaseException) {
            LoginOutcome.Failure(AdminAuthError.Unknown(e.message))
        }

    override suspend fun logout() {
        auth.signOut()
    }
}
