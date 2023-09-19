package de.bitb.pantryplaner.ui

import androidx.compose.ui.test.junit4.ComposeTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.bitb.pantryplaner.data.source.RemoteService
import de.bitb.pantryplaner.test.ScenarioData
import de.bitb.pantryplaner.test.mockDefaultUserDao
import de.bitb.pantryplaner.test.mockUserDao
import io.cucumber.java.en.Given
import javax.inject.Inject

@HiltAndroidTest
class MockingSteps(
    val scenarioData: ScenarioData,
) : ComposeTestRule by scenarioData.composeRule {

    @Inject
    lateinit var remoteService: RemoteService

    @Given("Mock nothing")
    fun mockNothing() = remoteService.mockUserDao()

    @Given("Mock default User")
    fun mockDefaultUser() = remoteService.mockDefaultUserDao()

}