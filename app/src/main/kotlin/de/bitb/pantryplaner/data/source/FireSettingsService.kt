package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FireSettingsService(
    private val fireConfig: FirebaseRemoteConfig,
    private val firestore: FirebaseFirestore,
) : SettingsRemoteDao {

    private val collection
        get() = firestore
            .collection("stage")
            .document(BuildConfig.FLAVOR)
            .collection("settings")

    private suspend fun getConfigValue(key: String): String {
        fireConfig.fetchAndActivate().await()
        return fireConfig.getString("${key}_${BuildConfig.FLAVOR.uppercase()}")
    }

    override suspend fun getAppVersion(): Resource<String> {
        return tryIt {
            val version = getConfigValue("app_version")
            Resource.Success(version.ifEmpty { BuildConfig.VERSION_NAME })
        }
    }

    override suspend fun getAppDownloadURL(): Resource<String> {
        return tryIt {
            val version = getConfigValue("app_download_url")
            Resource.Success(version.ifEmpty { "https://www.youtube.com/watch?v=xvFZjo5PgG0" })
        }
    }

    override fun getSettings(userId: String): Flow<Resource<Settings>> {
        return collection
            .whereEqualTo("uuid", userId)
            .snapshots()
            .map {
                tryIt {
                    val obj = it.toObjects(Settings::class.java).firstOrNull() ?: Settings(userId)
                    Resource.Success(obj)
                }
            }
            .distinctUntilChanged()
    }

    override suspend fun saveSettings(settings: Settings): Resource<Unit> {
        return tryIt {
            val ref = collection
                .whereEqualTo("uuid", settings.uuid)
                .get().await()
                .documents.firstOrNull()
            if (ref == null) collection.document().set(settings)
            else ref.reference.set(settings)
            Resource.Success()
        }
    }
}