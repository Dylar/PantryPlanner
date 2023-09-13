package de.bitb.pantryplaner.core

import android.app.Application
import android.content.Context
import dagger.hilt.android.testing.HiltTestApplication
import io.cucumber.android.hilt.HiltObjectFactory
import io.cucumber.android.runner.CucumberAndroidJUnitRunner
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["features"],
    glue = ["de.bitb.pantryplaner"],
    dryRun = true,
    strict = true,
    objectFactory = HiltObjectFactory::class
)
class GherkinsRunner : CucumberAndroidJUnitRunner() {
    @Throws(ClassNotFoundException::class,
        IllegalAccessException::class,
        InstantiationException::class)
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?,
    ): Application? = super.newApplication(cl, HiltTestApplication::class.java.name, context)
}
//
//class ActivityScenarioHolder {
//
//    private var scenario: ActivityScenario<*>? = null
//
//    fun launch(intent: Intent) {
//        scenario = ActivityScenario.launch<Activity>(intent)
//    }
//
//    /**
//     *  Close activity after scenario
//     */
//    @After
//    fun close() {
//        scenario?.close()
//    }
//}
//@WithJunitRule
//class ComposeRuleHolder {
//
//    @get:Rule
//    val composeRule = createEmptyComposeRule()
//}
//
//@WithJunitRule(useAsTestClassInDescription = true)
//@HiltAndroidTest
//class HiltRuleHolder {
//
//    @Rule(order = 0)
//    @JvmField
//    val hiltRule = HiltAndroidRule(this)
//
//    @Before
//    fun init() {
//        hiltRule.inject()
//    }
//
//}