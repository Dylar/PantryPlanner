package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.asResourceError
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

interface CheckRepository {
    fun getCheckLists(uuids: List<String>? = null): Flow<Resource<List<Checklist>>>
    fun getCheckList(uuid: String): Flow<Resource<Checklist>>
    suspend fun addChecklist(check: Checklist): Resource<Boolean>
    suspend fun deleteChecklist(check: Checklist): Resource<Boolean>
    suspend fun saveChecklist(check: Checklist): Resource<Unit>
}

class CheckRepositoryImpl(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) : CheckRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCheckLists(uuids: List<String>?): Flow<Resource<List<Checklist>>> =
        remoteDB.getUser(listOf(localDB.getUser()))
            .flatMapLatest { resp ->
                if (resp is Resource.Error) return@flatMapLatest flow { emit(resp.castTo()) }

                val user = resp.data!!.firstOrNull() //TODO needed?
                    ?: return@flatMapLatest flow { emit("Benutzer nicht gefunden".asResourceError()) }
                remoteDB.getCheckLists(user.uuid, uuids)
            }.map { resp ->
                return@map castOnError(resp) {
                    val lists =
                        resp.data?.sortedWith(
                            compareBy<Checklist> { it.name }.thenBy { it.finished }
                        )
                    Resource.Success(lists)
                }
            }

    override fun getCheckList(uuid: String): Flow<Resource<Checklist>> {
        return getCheckLists(listOf(uuid)).map {
            if (it is Resource.Error) it.castTo()
            else Resource.Success(it.data!!.first())
        }
    }

    override suspend fun addChecklist(check: Checklist): Resource<Boolean> {
        val now = formatDateNow()
        val user = localDB.getUser()
        return remoteDB.addChecklist(check.copy(creator = user, createdAt = now, updatedAt = now))
    }

    override suspend fun deleteChecklist(check: Checklist): Resource<Boolean> =
        remoteDB.deleteChecklist(check)

    override suspend fun saveChecklist(check: Checklist): Resource<Unit> =
        remoteDB.saveChecklist(check.copy(updatedAt = formatDateNow()))
}
