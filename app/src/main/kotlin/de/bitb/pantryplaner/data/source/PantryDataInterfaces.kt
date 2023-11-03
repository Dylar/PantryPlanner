package de.bitb.pantryplaner.data.source

import de.bitb.pantryplaner.core.misc.Result
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
    suspend fun getAppVersion(): Result<String>
    suspend fun getAppDownloadURL(): Result<String>
    fun getSettings(userId: String): Flow<Result<Settings>>
    suspend fun saveSettings(settings: Settings): Result<Unit>
}

interface UserRemoteDao {
    suspend fun isUserLoggedIn(): Result<Boolean>
    suspend fun registerUser(email: String, pw: String): Result<Unit>
    suspend fun loginUser(email: String, pw: String): Result<Boolean>
    suspend fun logoutUser(): Result<Unit>
    fun getUser(uuids: List<String>): Flow<Result<List<User>>>
    suspend fun getUserByEmail(email: String): Result<User>
    suspend fun saveUser(user: User): Result<Unit>
}

interface ItemRemoteDao {
    fun getItems(ids: List<String>): Flow<Result<List<Item>>>
    fun getItems(userId: String, ids: List<String>?): Flow<Result<List<Item>>>
    suspend fun addItem(item: Item): Result<Boolean>
    suspend fun deleteItem(item: Item): Result<Boolean>
    suspend fun saveItems(items: List<Item>): Result<Unit>
}

interface CheckRemoteDao {
    fun getCheckLists(userId: String, ids: List<String>?): Flow<Result<List<Checklist>>>
    suspend fun addChecklist(check: Checklist): Result<Boolean>
    suspend fun deleteChecklist(check: Checklist): Result<Boolean>
    suspend fun saveChecklist(check: Checklist): Result<Unit>
}

interface StockRemoteDao {
    fun getStocks(userId: String): Flow<Result<List<Stock>>>
    suspend fun addStock(stock: Stock): Result<Boolean>
    suspend fun deleteStock(stock: Stock): Result<Boolean>
    suspend fun saveStocks(stocks: List<Stock>): Result<Unit>
}
