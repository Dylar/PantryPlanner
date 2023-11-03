package de.bitb.pantryplaner.data.source

import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.data.model.Stock
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
    StockRemoteDao,
    SettingsRemoteDao

class PantryRemoteService(
    settingsService: FireSettingsService,
    userService: FireUserService,
    itemService: FireItemService,
    checkService: FireCheckService,
    stockItemService: FireStockService,
) :
    RemoteService,
    UserRemoteDao by userService,
    ItemRemoteDao by itemService,
    CheckRemoteDao by checkService,
    StockRemoteDao by stockItemService,
    SettingsRemoteDao by settingsService

interface SettingsRemoteDao {
    suspend fun getAppVersion(): Resource<String>
    suspend fun getAppDownloadURL(): Resource<String>
    fun getSettings(userId: String): Flow<Resource<Settings>>
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
    fun getItems(ids: List<String>): Flow<Resource<List<Item>>>
    fun getItems(userId: String, ids: List<String>?): Flow<Resource<List<Item>>>
    suspend fun addItem(item: Item): Resource<Boolean>
    suspend fun deleteItem(item: Item): Resource<Boolean>
    suspend fun saveItems(items: List<Item>): Resource<Unit>
}

interface CheckRemoteDao {
    fun getCheckLists(userId: String, ids: List<String>?): Flow<Resource<List<Checklist>>>
    suspend fun addChecklist(check: Checklist): Resource<Boolean>
    suspend fun deleteChecklist(check: Checklist): Resource<Boolean>
    suspend fun saveChecklist(check: Checklist): Resource<Unit>
}

interface StockRemoteDao {
    fun getStocks(userId: String): Flow<Resource<List<Stock>>>
    suspend fun addStock(stock: Stock): Resource<Boolean>
    suspend fun deleteStock(stock: Stock): Resource<Boolean>
    suspend fun saveStocks(stocks: List<Stock>): Resource<Unit>
}
