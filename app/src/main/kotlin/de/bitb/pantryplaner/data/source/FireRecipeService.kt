package de.bitb.pantryplaner.data.source

import com.google.firebase.firestore.FirebaseFirestore
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.tryIt
import de.bitb.pantryplaner.data.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FireRecipeService(
    firestore: FirebaseFirestore,
) : RecipeRemoteDao {

    private val recipeCollection = firestore
        .collection("stage")
        .document(BuildConfig.FLAVOR)
        .collection("recipes")

    private fun ownerCollection(id: String) =
        recipeCollection.whereEqualTo("creator", id)

    private fun sharedCollection(id: String) =
        recipeCollection.whereArrayContains("sharedWith", id)

    override fun getRecipes(
        userId: String,
        ids: List<String>?,
    ): Flow<Result<List<Recipe>>> {
        return getOwnedOrShared(userId, ids, ::ownerCollection, ::sharedCollection)
    }

    override suspend fun saveRecipe(recipe: Recipe): Result<Unit> {
        return tryIt {
            recipeCollection
                .whereEqualTo("uuid", recipe.uuid)
                .get().await()
                .documents.first()
                .reference.set(recipe)
            Result.Success()
        }
    }

    override suspend fun addRecipe(recipe: Recipe): Result<Boolean> {
        return tryIt {
            val querySnapshot = recipeCollection
                .whereEqualTo("uuid", recipe.uuid)
                .get().await()

            if (querySnapshot.isEmpty) {
                recipeCollection.add(recipe).await()
                Result.Success(true)
            } else {
                Result.Success(false)
            }
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe): Result<Boolean> {
        return tryIt {
            val querySnapshot = recipeCollection
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