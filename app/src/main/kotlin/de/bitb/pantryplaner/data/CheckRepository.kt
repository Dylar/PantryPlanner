package de.bitb.pantryplaner.data

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.source.RemoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CheckRepository {
    fun getCheckLists(): Flow<Resource<List<Checklist>>>
    suspend fun addChecklist(check: Checklist): Resource<Boolean>
    suspend fun removeChecklist(check: Checklist): Resource<Boolean>
    suspend fun saveChecklist(check: Checklist): Resource<Unit>
}

class CheckRepositoryImpl(
    private val remoteDB: RemoteService,
) : CheckRepository {

    override fun getCheckLists(): Flow<Resource<List<Checklist>>> = remoteDB.getCheckLists()
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

    override suspend fun addChecklist(check: Checklist): Resource<Boolean> = remoteDB.addChecklist(check)
    override suspend fun removeChecklist(check: Checklist): Resource<Boolean> = remoteDB.removeChecklist(check)
    override suspend fun saveChecklist(check: Checklist): Resource<Unit> = remoteDB.saveChecklist(check)
}
