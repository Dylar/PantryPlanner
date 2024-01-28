package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.core.misc.asError
import de.bitb.pantryplaner.core.misc.castOnError
import de.bitb.pantryplaner.core.misc.formatDateNow
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.source.LocalDatabase
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CheckRepository(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCheckLists(uuids: List<String>? = null): Flow<Result<List<Checklist>>> =
        remoteDB.getUser(listOf(localDB.getUser()))
            .flatMapLatest { resp ->
                if (resp is Result.Error) return@flatMapLatest flow { emit(resp.castTo()) }

                val user = resp.data!!.firstOrNull() //TODO needed?
                    ?: return@flatMapLatest flow { emit("Benutzer nicht gefunden".asError()) }
                remoteDB.getCheckLists(user.uuid, uuids)
            }.map { resp ->
                return@map castOnError(resp) {
                    val lists =
                        resp.data?.sortedWith(
                            compareBy<Checklist> { it.name }.thenBy { it.finished }
                        )
                    Result.Success(lists)
                }
            }

    fun getCheckList(uuid: String): Flow<Result<Checklist>> {
        return getCheckLists(listOf(uuid)).map {
            if (it is Result.Error) it.castTo()
            else Result.Success(it.data?.firstOrNull())
        }
    }

    suspend fun addChecklist(check: Checklist): Result<Boolean> {
        val now = formatDateNow()
        val user = localDB.getUser()
        return remoteDB.addChecklist(check.copy(creator = user, createdAt = now, updatedAt = now))
    }

    suspend fun deleteChecklist(check: Checklist): Result<Boolean> =
        remoteDB.deleteChecklist(check)

    suspend fun saveChecklist(check: Checklist): Result<Unit> =
        remoteDB.saveChecklist(check.copy(updatedAt = formatDateNow()))
}
