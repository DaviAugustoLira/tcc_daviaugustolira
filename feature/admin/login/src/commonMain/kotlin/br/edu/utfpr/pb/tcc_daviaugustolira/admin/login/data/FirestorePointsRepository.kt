package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.data

import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.CreatePointError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.CreatePointOutcome
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.NewPoint
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.Point
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.points.PointsRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseException
import dev.gitlive.firebase.FirebaseNetworkException
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirestorePointsRepository(
    private val firestore: FirebaseFirestore = Firebase.firestore,
) : PointsRepository {
    override fun observePoints(mapId: String): Flow<List<Point>> =
        firestore.collection(POINTS_COLLECTION).where { "mapId" equalTo mapId }.snapshots.map { snapshot ->
            snapshot.documents.mapNotNull { document ->
                document.data<PointDto>().toDomain(id = document.id).getOrNull()
            }
        }

    override suspend fun createPoint(
        point: NewPoint,
        createdByUid: String,
    ): CreatePointOutcome =
        try {
            firestore.collection(POINTS_COLLECTION).add(
                NewPointDto(
                    mapId = point.mapId,
                    name = point.name,
                    description = point.description,
                    x = point.x.toLong(),
                    y = point.y.toLong(),
                    createdBy = createdByUid,
                ),
            )
            CreatePointOutcome.Success
        } catch (_: FirebaseNetworkException) {
            CreatePointOutcome.Failure(CreatePointError.NetworkUnavailable)
        } catch (e: FirebaseException) {
            CreatePointOutcome.Failure(CreatePointError.Unknown(e.message))
        }

    private companion object {
        const val POINTS_COLLECTION = "points"
    }
}
