package de.bitb.pantryplaner.data.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

    override suspend fun isUserLoggedIn(): Result<Boolean> {
        return tryIt {
            val user = fireAuth.currentUser //TODO user infos into firestore
            Result.Success(user != null)
        }
    }

    override suspend fun registerUser(email: String, pw: String): Result<Unit> {
        return tryIt {
            val authResult = fireAuth.createUserWithEmailAndPassword(email, pw).await()
            if (authResult.user != null) Result.Success()
            else "Not registered".asError()
        }
    }

    override suspend fun loginUser(email: String, pw: String): Result<Boolean> {
        return tryIt(false) {
            val authResult = fireAuth.signInWithEmailAndPassword(email, pw).await()
            Result.Success(authResult.user != null)
        }
    }

    override suspend fun logoutUser(): Result<Unit> {
        return tryIt {
            fireAuth.signOut()
            Result.Success()
        }
    }

    override fun getUser(uuids: List<String>): Flow<Result<List<User>>> {
        return try {
            return collection
                .whereIn("uuid", uuids)
                .snapshots()
                .map {
                    val user = it.toObjects(User::class.java)
                    Result.Success(user)
                }
        } catch (e: Exception) {
            MutableStateFlow(e.asError())
        }
    }

    override suspend fun getUserByEmail(email: String): Result<User> {
        return tryIt {
            val snap = collection
                .whereEqualTo("email", email)
                .get().await()
            Result.Success(snap.toObjects(User::class.java).firstOrNull())
        }
    }

    override suspend fun saveUser(user: User): Result<Unit> {
        return tryIt {
            collection
                .whereEqualTo("uuid", user.uuid)
                .get().await()
                .documents.firstOrNull()
                ?.reference?.set(user) ?: collection.add(user)
            Result.Success()
        }
    }
}