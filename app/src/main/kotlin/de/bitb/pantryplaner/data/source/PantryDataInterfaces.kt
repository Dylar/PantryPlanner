package de.bitb.pantryplaner.data.source

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.data.model.User
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

// REMOTE
interface RemoteService : UserRemoteDao, ItemRemoteDao, CheckRemoteDao, SettingsRemoteDao

class PantryRemoteService(
    settingsService: FireSettingsService,
    userService: FireUserService,
    itemService: FireItemService,
    checkService: FireCheckService
) :
    RemoteService,
    UserRemoteDao by userService,
    ItemRemoteDao by itemService,
    CheckRemoteDao by checkService,
    SettingsRemoteDao by settingsService

interface SettingsRemoteDao {
    fun getSettings(): Flow<Resource<Settings>>
    suspend fun saveSettings(settings: Settings): Resource<Unit>
}

interface UserRemoteDao {
    suspend fun isUserLoggedIn(): Resource<Boolean>
    suspend fun registerUser(email: String, pw: String): Resource<Unit>
    suspend fun loginUser(email: String, pw: String): Resource<Boolean>
    suspend fun logoutUser(): Resource<Unit>
    suspend fun getUser(uuid: String): Resource<User?>
    suspend fun getUserByEmail(email: String): Resource<User>
    suspend fun saveUser(user: User): Resource<Unit>
}

interface ItemRemoteDao {
    fun getItems(ids: List<String>?): Flow<Resource<List<Item>>>
    suspend fun addItem(item: Item): Resource<Boolean>
    suspend fun removeItem(item: Item): Resource<Boolean>
    suspend fun saveItems(items: List<Item>): Resource<Unit>
}

interface CheckRemoteDao {
    fun getCheckLists(ids: List<String>?): Flow<Resource<List<Checklist>>>
    suspend fun addChecklist(check: Checklist): Resource<Boolean>
    suspend fun removeChecklist(check: Checklist): Resource<Boolean>
    suspend fun saveChecklist(check: Checklist): Resource<Unit>
}
