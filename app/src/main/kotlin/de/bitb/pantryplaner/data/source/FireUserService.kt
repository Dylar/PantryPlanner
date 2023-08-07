package de.bitb.pantryplaner.data.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FireUserService(
    private val firestore: FirebaseFirestore,
    private val fireAuth: FirebaseAuth,
) : UserRemoteDao {

    private val collection
        get() = firestore
            .collection("stage")
            .document(BuildConfig.FLAVOR)
            .collection("user")

    override suspend fun isUserLoggedIn(): Resource<Boolean> {
        return tryIt {
            val user = fireAuth.currentUser
            Resource.Success(user != null)
        }
    }

    override suspend fun loginUser(): Resource<Boolean> {
        return tryIt(
            onError = { Resource.Error(it, false) },
            onTry = {
                fireAuth.signInAnonymously().await()
                Resource.Success(true)
            },
        )
    }
}