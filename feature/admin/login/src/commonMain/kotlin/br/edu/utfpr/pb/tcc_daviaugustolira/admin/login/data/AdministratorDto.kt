package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import dev.gitlive.firebase.firestore.BaseTimestamp
import kotlinx.serialization.Serializable

/**
 * Espelho do documento `administrators/{id}` (ver `docs/firestore-schema.md`). Só leitura: a
 * escrita desta coleção é sempre negada a clients (cadastro/edição de administrador concede
 * acesso a todo o resto do schema, então exige Console/Cloud Function) — por isso não existe
 * `NewAdministratorDto`. `role` decide visibilidade (qual `sector` este admin enxerga);
 * `permissions` decide ação dentro do que já é visível — os dois nunca decidem a mesma coisa.
 */
@Serializable
data class AdministratorDto(
    val name: String? = null,
    val email: String? = null,
    val role: String? = null,
    val permissions: List<String>? = null,
    val sector: String? = null,
    val createdAt: BaseTimestamp? = null,
    val updatedAt: BaseTimestamp? = null,
    val lastAccess: BaseTimestamp? = null,
)
