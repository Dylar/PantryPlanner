package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.castOnError
import de.bitb.pantryplaner.core.misc.formatDateNow
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.Recipe
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class RecipeRepository(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) {

    fun getUserRecipes(
        ids: List<String>? = null,
    ): Flow<Result<List<Recipe>>> = remoteDB.getRecipes(localDB.getUser(), ids)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getRecipes(uuids: List<String>? = null): Flow<Result<List<Recipe>>> =
        remoteDB.getUser(listOf(localDB.getUser()))
            .flatMapLatest { resp ->
                if (resp is Result.Error) return@flatMapLatest flow { emit(resp.castTo()) }
                val user = resp.data!!.firstOrNull() //TODO needed?
                    ?: return@flatMapLatest flow { emit("Benutzer nicht gefunden".asError()) }
                remoteDB.getRecipes(user.uuid, uuids)
            }.map { resp ->
                return@map castOnError(resp) {
                    val lists = resp.data?.sortedWith(compareBy { it.name })
                    Result.Success(lists)
                }
            }

    fun getRecipe(uuid: String): Flow<Result<Recipe>> {
        return getRecipes(listOf(uuid)).map {
            if (it is Result.Error) it.castTo()
            else Result.Success(it.data?.firstOrNull())
        }
    }

    suspend fun addRecipe(recipe: Recipe): Result<Boolean> {
        val now = formatDateNow()
        val user = localDB.getUser()
        return remoteDB.addRecipe(recipe.copy(creator = user, createdAt = now, updatedAt = now))
    }

    suspend fun deleteRecipe(recipe: Recipe): Result<Boolean> =
        remoteDB.deleteRecipe(recipe)

    suspend fun saveRecipe(recipe: Recipe): Result<Unit> = saveRecipes(listOf(recipe))

    suspend fun saveRecipes(recipes: List<Recipe>): Result<Unit> =
        remoteDB.saveRecipes(recipes.map { it.copy(updatedAt = formatDateNow()) })

}
