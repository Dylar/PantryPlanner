package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FireRecipeService(
    val firestore: FirebaseFirestore,
) : RecipeRemoteDao {

    private val collection = firestore
        .collection("stage")
        .document(BuildConfig.FLAVOR)
        .collection("recipes")

    private fun ownerCollection(id: String) =
        collection.whereEqualTo("creator", id)

    private fun sharedCollection(id: String) =
        collection.whereArrayContains("sharedWith", id)

    override fun getRecipes(
        userId: String,
        ids: List<String>?,
    ): Flow<Result<List<Recipe>>> {
        return getOwnedOrShared(userId, ids, ::ownerCollection, ::sharedCollection)
    }

    override suspend fun saveRecipes(recipes: List<Recipe>): Result<Unit> {
        return tryIt {
            firestore.batch().apply {
                recipes.chunked(10).forEach { chunk ->
                    collection
                        .whereIn("uuid", chunk.map { it.uuid })
                        .get().await().documents
                        .forEach { snap ->
                            val uuid = snap.data?.get("uuid") ?: ""
                            set(snap.reference, chunk.first { it.uuid == uuid })
                        }
                }
                commit()
            }
            Result.Success()
        }
    }

    override suspend fun addRecipe(recipe: Recipe): Result<Boolean> {
        return tryIt {
            val querySnapshot = collection
                .whereEqualTo("uuid", recipe.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                collection.add(recipe).await()
                Result.Success(true)
            } else {
                Result.Success(false)
            }
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe): Result<Boolean> {
        return tryIt {
            val querySnapshot = collection
                .whereEqualTo("uuid", recipe.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                Result.Success(false)
            } else {
                querySnapshot.documents.first().reference.delete().await()
                Result.Success(true)
            }
        }
    }

}