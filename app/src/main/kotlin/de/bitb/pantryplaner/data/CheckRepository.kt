package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.castOnError
import de.bitb.pantryplaner.core.misc.formatDateNow
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CheckRepository {
    fun getCheckLists(uuids: List<String>? = null): Flow<Resource<List<Checklist>>>
    fun getCheckList(uuid: String): Flow<Resource<Checklist>>
    suspend fun addChecklist(check: Checklist): Resource<Boolean>
    suspend fun removeChecklist(check: Checklist): Resource<Boolean>
    suspend fun saveChecklist(check: Checklist): Resource<Unit>
}

class CheckRepositoryImpl(
    private val remoteDB: RemoteService,
) : CheckRepository {

    override fun getCheckLists(uuids: List<String>?): Flow<Resource<List<Checklist>>> =
        remoteDB.getCheckLists(uuids)
            .map { resp ->
                return@map castOnError(resp) {
                    val lists = resp.data
                    lists?.sortedBy { it.name }
                    lists?.sortedBy { it.finished }
                    Resource.Success(lists)
                }
            }

    override fun getCheckList(uuid: String): Flow<Resource<Checklist>> {
        return getCheckLists(listOf(uuid)).map {
            if (it is Resource.Error) it.castTo<Checklist>()
            else Resource.Success(it.data!!.first())
        }
    }

    override suspend fun addChecklist(check: Checklist): Resource<Boolean> {
        val now = formatDateNow()
        return remoteDB.addChecklist(check.copy(createdAt = now, updatedAt = now))
    }

    override suspend fun removeChecklist(check: Checklist): Resource<Boolean> =
        remoteDB.removeChecklist(check)

    override suspend fun saveChecklist(check: Checklist): Resource<Unit> =
        remoteDB.saveChecklist(check.copy(updatedAt = formatDateNow()))
}
