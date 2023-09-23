package de.bitb.pantryplaner.data.source

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.Location
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.model.StockItem
import de.bitb.pantryplaner.data.model.User
import kotlinx.coroutines.flow.Flow

interface LocalDatabase {
    fun setUser(uuid: String)
    fun getUser(): String
}

// REMOTE
interface RemoteService :
    UserRemoteDao,
    ItemRemoteDao,
    CheckRemoteDao,
    StockItemRemoteDao,
    LocationRemoteDao,
    SettingsRemoteDao

class PantryRemoteService(
    settingsService: FireSettingsService,
    userService: FireUserService,
    itemService: FireItemService,
    checkService: FireCheckService,
    stockItemService: FireStockService,
    locationService: FireLocationService,
) :
    RemoteService,
    UserRemoteDao by userService,
    ItemRemoteDao by itemService,
    CheckRemoteDao by checkService,
    StockItemRemoteDao by stockItemService,
    LocationRemoteDao by locationService,
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
    fun getUser(uuids: List<String>): Flow<Resource<List<User>>>
    suspend fun getUserByEmail(email: String): Resource<User>
    suspend fun saveUser(user: User): Resource<Unit>
}

interface ItemRemoteDao {
    fun getItems(userId: String, ids: List<String>?): Flow<Resource<List<Item>>>
    suspend fun addItem(item: Item): Resource<Boolean>
    suspend fun deleteItem(userId: String, item: Item): Resource<Boolean>
    suspend fun saveItems(userId: String, items: List<Item>): Resource<Unit>
}

interface CheckRemoteDao {
    fun getCheckLists(userId: String, ids: List<String>?): Flow<Resource<List<Checklist>>>
    suspend fun addChecklist(check: Checklist): Resource<Boolean>
    suspend fun deleteChecklist(userId: String, check: Checklist): Resource<Boolean>
    suspend fun saveChecklist(userId: String, check: Checklist): Resource<Unit>
}

interface StockItemRemoteDao {
    fun getStock(userId: String): Flow<Resource<Stock>>
    suspend fun addStockItem(userId: String, item: StockItem): Resource<Boolean>
    suspend fun deleteStockItem(userId: String, item: StockItem): Resource<Boolean>
    suspend fun saveStock(userId: String, stock: Stock): Resource<Unit>
}

interface LocationRemoteDao {
    fun getLocations(userId: String): Flow<Resource<List<Location>>>
    suspend fun addLocation(location: Location): Resource<Boolean>
}
