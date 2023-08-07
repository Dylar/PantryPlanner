package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FireSettingsService(
    private val firestore: FirebaseFirestore,
) : SettingsRemoteDao {

    private val collection
        get() = firestore
            .collection("stage")
            .document(BuildConfig.FLAVOR)
            .collection("settings")

    override fun getSettings(): Flow<Resource<Settings>> {
        return collection
            .snapshots()
            .map {
                tryIt {
                    Resource.Success(
                        it.toObjects(Settings::class.java).firstOrNull() ?: Settings()
                    )
                }
            }
    }

    override suspend fun saveSettings(settings: Settings): Resource<Unit> {
        return tryIt {
            val ref = collection
                .get().await()
                .documents.firstOrNull()
            if (ref == null) collection.document().set(settings)
            else ref.reference.set(settings)
            Resource.Success()
        }
    }
}