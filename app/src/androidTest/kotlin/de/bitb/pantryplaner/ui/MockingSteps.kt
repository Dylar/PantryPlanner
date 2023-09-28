package de.bitb.pantryplaner.ui

import androidx.compose.ui.test.junit4.ComposeTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.data.source.RemoteService
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.test.defaultPW
import de.bitb.pantryplaner.test.mockLocationDao
import de.bitb.pantryplaner.test.mockUserDao
import de.bitb.pantryplaner.test.parseLocationCreator
import de.bitb.pantryplaner.test.parseLocationShared
import de.bitb.pantryplaner.test.parseUser
import de.bitb.pantryplaner.test.parseUserConnected
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
        remoteService.mockUserDao()
        remoteService.mockLocationDao()
    }

    @Given("Init default Mocks")
    fun initDefaultMocks() {
        initMocks()
        mockDefaultUsers()
        mockDefaultLocations()
    }

    @Given("Mock default Users")
    fun mockDefaultUsers() {
        val user1 = parseUser()
        val user2 = parseUserConnected()
        val user3 = parseUserOther()
        val map = mutableMapOf(
            user1.email to defaultPW,
            user2.email to defaultPW,
            user3.email to defaultPW,
        )
        remoteService.mockUserDao(listOf(user1, user2, user3).toMutableList(), map)
    }

    @Given("Mock default Locations")
    fun mockDefaultLocations() {
        val loc1 = parseLocationCreator()
        val loc2 = parseLocationShared()
        remoteService.mockLocationDao(listOf(loc1, loc2))
    }

}