package de.bitb.pantryplaner.data.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.User
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

    override suspend fun registerUser(email: String, pw: String): Resource<Unit> =
        tryIt {
            val authResult = fireAuth.createUserWithEmailAndPassword(email, pw).await()
            if (authResult.user != null) Resource.Success() else "Not registered".asResourceError()
        }

    override suspend fun loginUser(email: String, pw: String): Resource<Boolean> {
        return tryIt {
            val authResult = fireAuth.signInWithEmailAndPassword(email, pw).await()
            Resource.Success(authResult.user != null)
        }
    }

    override suspend fun logoutUser(): Resource<Unit> {
        return tryIt {
            fireAuth.signOut()
            Resource.Success()
        }
    }

    override suspend fun getUser(uuid: String): Resource<User?> {
        return tryIt {
            val snap = collection
                .whereEqualTo("uuid", uuid)
                .get().await()
            Resource.Success(snap.toObjects(User::class.java).firstOrNull())
        }
    }

    override suspend fun saveUser(user: User): Resource<Unit> {
        return tryIt {
            collection
                .whereEqualTo("uuid", user.uuid)
                .get().await()
                .documents.firstOrNull()
                ?.reference?.set(user) ?: collection.add(user)
            Resource.Success()
        }
    }
}