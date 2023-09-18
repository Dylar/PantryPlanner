package de.bitb.pantryplaner.test

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import dagger.hilt.android.testing.HiltTestApplication
import de.bitb.pantryplaner.core.MainActivity
//import io.cucumber.android.hilt.HiltObjectFactory
import io.cucumber.android.runner.CucumberAndroidJUnitRunner
import io.cucumber.java.After
import io.cucumber.junit.CucumberOptions
import io.cucumber.junit.WithJunitRule
import org.junit.Rule

@CucumberOptions(
    features = ["features"],
    glue = ["de.bitb.pantryplaner"],
)
class GherkinsRunner : CucumberAndroidJUnitRunner() {
    @Throws(
        ClassNotFoundException::class,
        IllegalAccessException::class,
        InstantiationException::class
    )
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?,
    ): Application? = super.newApplication(cl, HiltTestApplication::class.java.name, context)
}

@WithJunitRule
class ScenarioData {
    @get:Rule(order = 1)
    val composeRule = createEmptyComposeRule()

    private var scenario: ActivityScenario<*>? = null
    private val data: MutableMap<String, Any> = mutableMapOf()

    @After
    fun close() = scenario?.close()

    fun launch(intent: Intent? = null) {
        scenario = launchActivity<MainActivity>(intent)
    }

    fun saveUser(email: String, password: String) {
        data["email"] = email
        data["password"] = password
    }

}