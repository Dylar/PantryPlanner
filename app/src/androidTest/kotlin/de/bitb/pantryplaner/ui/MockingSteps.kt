package de.bitb.pantryplaner.ui

import androidx.compose.ui.test.junit4.ComposeTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.data.source.RemoteService
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.test.defaultPW
import de.bitb.pantryplaner.test.mockChecklistDao
import de.bitb.pantryplaner.test.mockItemDao
import de.bitb.pantryplaner.test.mockRecipeDao
import de.bitb.pantryplaner.test.mockSettingsDao
import de.bitb.pantryplaner.test.mockStockDao
import de.bitb.pantryplaner.test.mockUserDao
import de.bitb.pantryplaner.test.parseChecklistCreator
import de.bitb.pantryplaner.test.parseChecklistFinished
import de.bitb.pantryplaner.test.parseChecklistShared
import de.bitb.pantryplaner.test.parseItemCreator
import de.bitb.pantryplaner.test.parseItemSelect
import de.bitb.pantryplaner.test.parseItemShared
import de.bitb.pantryplaner.test.parseItemUnshared
import de.bitb.pantryplaner.test.parseRecipeCreator
import de.bitb.pantryplaner.test.parseRecipeShared
import de.bitb.pantryplaner.test.parseSettings
import de.bitb.pantryplaner.test.parseStockCreator
import de.bitb.pantryplaner.test.parseStockShared
import de.bitb.pantryplaner.test.parseUser
import de.bitb.pantryplaner.test.parseUserConnected
import de.bitb.pantryplaner.test.parseUserExcludie
import de.bitb.pantryplaner.test.parseUserOther
import io.cucumber.java.en.Given
import javax.inject.Inject

@HiltAndroidTest
class MockingSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Inject
    lateinit var remoteService: RemoteService

    @Given("Init Mocks")
    fun initMocks() {
        remoteService.mockSettingsDao()
        remoteService.mockUserDao()
        remoteService.mockStockDao()
        remoteService.mockItemDao()
        remoteService.mockChecklistDao()
        remoteService.mockRecipeDao()
    }

    @Given("Mock App Version {string}")
    fun mockAppVersion(version: String) {
        remoteService.mockSettingsDao(version = version)
    }

    @Given("Init default Mocks")
    fun initDefaultMocks() {
        initMocks()
        mockDefaultSettings()
        mockDefaultUsers()
        mockDefaultStocks()
        mockDefaultItems()
        mockDefaultChecklists()
        mockDefaultRecipes()
    }

    @Given("Mock default Settings")
    fun mockDefaultSettings() {
        val settings = parseSettings()
        remoteService.mockSettingsDao(settings)
    }

    @Given("Mock default Users")
    fun mockDefaultUsers() {
        val user1 = parseUser()
        val user2 = parseUserConnected()
        val user3 = parseUserOther()
        val user4 = parseUserExcludie()
        val pwMap = mutableMapOf(
            user1.email to defaultPW,
            user2.email to defaultPW,
            user3.email to defaultPW,
            user4.email to defaultPW,
        )
        remoteService.mockUserDao(listOf(user1, user2, user3, user4).toMutableList(), pwMap)
    }

    @Given("Mock default Stocks")
    fun mockDefaultStocks() {
        val stock1 = parseStockCreator()
        val stock2 = parseStockShared()
        remoteService.mockStockDao(listOf(stock1, stock2))
    }

    @Given("Mock default Items")
    fun mockDefaultItems() {
        val item1 = parseItemCreator()
        val item2 = parseItemShared()
        val item3 = parseItemSelect()
        val item4 = parseItemUnshared()
        remoteService.mockItemDao(mutableListOf(item1, item2, item3, item4))
    }

    @Given("Mock default Checklists")
    fun mockDefaultChecklists() {
        val check1 = parseChecklistCreator()
        val check2 = parseChecklistShared()
        val check3 = parseChecklistFinished()
        remoteService.mockChecklistDao(listOf(check1, check2, check3))
    }

    @Given("Mock default Recipes")
    fun mockDefaultRecipes() {
        val recipe1 = parseRecipeCreator()
        val recipe2 = parseRecipeShared()
        remoteService.mockRecipeDao(listOf(recipe1, recipe2))
    }
}