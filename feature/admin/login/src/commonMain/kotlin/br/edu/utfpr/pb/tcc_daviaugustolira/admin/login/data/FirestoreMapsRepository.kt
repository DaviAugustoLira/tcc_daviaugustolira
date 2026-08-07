package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.CreateMapError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.CreateMapOutcome
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.IndoorMap
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.MapsRepository
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.maps.NewMap
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseException
import dev.gitlive.firebase.FirebaseNetworkException
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

    override suspend fun createMap(
        map: NewMap,
        createdByUid: String,
    ): CreateMapOutcome =
        try {
            firestore.collection(MAPS_COLLECTION).add(
                NewMapDto(
                    name = map.name,
                    description = map.description,
                    svgUrl = map.svgUrl,
                    scale = map.scale,
                    floor = map.floor,
                    createdBy = createdByUid,
                ),
            )
            CreateMapOutcome.Success
        } catch (_: FirebaseNetworkException) {
            CreateMapOutcome.Failure(CreateMapError.NetworkUnavailable)
        } catch (e: FirebaseException) {
            CreateMapOutcome.Failure(CreateMapError.Unknown(e.message))
        }

    private companion object {
        const val MAPS_COLLECTION = "maps"
    }
}
