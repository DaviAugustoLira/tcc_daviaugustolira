package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.IndoorMap
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.MapsRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirestoreMapsRepository(
    private val firestore: FirebaseFirestore = Firebase.firestore,
) : MapsRepository {
    override fun observeMaps(): Flow<List<IndoorMap>> =
        firestore.collection(MAPS_COLLECTION).snapshots.map { snapshot ->
            snapshot.documents.mapNotNull { document ->
                document.data<MapDto>().toDomain(id = document.id).getOrNull()
            }
        }

    private companion object {
        const val MAPS_COLLECTION = "maps"
    }
}
