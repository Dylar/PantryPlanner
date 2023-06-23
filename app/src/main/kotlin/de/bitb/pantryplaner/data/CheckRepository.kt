package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
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
            .map { res ->
                if (res is Resource.Error) {
                    res
                } else {
                    Resource.Success(res.data?.apply {
                        sortedBy { it.name }
                        sortedBy { it.finished }
                    })
                }
            }

    override fun getCheckList(uuid: String): Flow<Resource<Checklist>> {
        return getCheckLists(listOf(uuid)).map {
            if (it is Resource.Error) {
                return@map it.castTo<Checklist>()
            }
            Resource.Success(it.data!!.first())
        }
    }

    override suspend fun addChecklist(check: Checklist): Resource<Boolean> =
        remoteDB.addChecklist(check)

    override suspend fun removeChecklist(check: Checklist): Resource<Boolean> =
        remoteDB.removeChecklist(check)

    override suspend fun saveChecklist(check: Checklist): Resource<Unit> =
        remoteDB.saveChecklist(check)
}
