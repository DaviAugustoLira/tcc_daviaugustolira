package br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps

import kotlinx.coroutines.flow.Flow

/**
 * Porta para a collection `maps` do Firestore. Vive em `shared/domain` (não em uma
 * feature) porque o radiomap é conceitualmente consumido por mais de um contexto —
 * hoje o painel admin lista os maps, e a navegação do usuário final vai precisar do
 * mesmo dado no futuro (ver skill firestore-collection) — mesmo que só exista uma
 * implementação/consumidor concreto por enquanto.
 */
interface MapsRepository {
    fun observeMaps(): Flow<List<IndoorMap>>
}
