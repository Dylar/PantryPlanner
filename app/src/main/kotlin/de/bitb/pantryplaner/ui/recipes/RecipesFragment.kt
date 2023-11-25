package de.bitb.pantryplaner.ui.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.data.model.Recipe
import de.bitb.pantryplaner.data.model.groupByCategory
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.NaviEvent
import de.bitb.pantryplaner.ui.base.comps.DismissItem
import de.bitb.pantryplaner.ui.base.comps.EmptyListComp
import de.bitb.pantryplaner.ui.base.comps.ErrorScreen
import de.bitb.pantryplaner.ui.base.comps.FloatingExpandingButton
import de.bitb.pantryplaner.ui.base.comps.GridListLayout
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.testTags.RecipeTag
import de.bitb.pantryplaner.ui.base.testTags.RecipesPageTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import de.bitb.pantryplaner.ui.checklists.ChecklistsFragment
import de.bitb.pantryplaner.ui.comps.buildBottomNavi
import de.bitb.pantryplaner.ui.profile.ProfileFragment
import de.bitb.pantryplaner.ui.recipes.details.RecipeDetailsFragment
import de.bitb.pantryplaner.ui.settings.SettingsFragment
import de.bitb.pantryplaner.ui.stock.StocksFragment

@AndroidEntryPoint
class RecipesFragment : BaseFragment<RecipesViewModel>() {

    companion object {
        val naviFromChecklists: NaviEvent = NaviEvent.Navigate(R.id.checklists_to_recipes)
        val naviFromStocks: NaviEvent = NaviEvent.Navigate(R.id.stocks_to_recipes)
        val naviFromProfile: NaviEvent = NaviEvent.Navigate(R.id.profile_to_recipes)
        val naviFromSettings: NaviEvent = NaviEvent.Navigate(R.id.settings_to_recipes)
    }

    override val viewModel: RecipesViewModel by viewModels()

    private lateinit var showGridLayout: MutableState<Boolean>

    @Composable
    override fun screenContent() {
        showGridLayout = remember { mutableStateOf(true) }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            content = { buildContent(it) },
            floatingActionButton = { buildFab() },
            bottomBar = {
                buildBottomNavi(
                    checklistsRoute = ChecklistsFragment.naviFromRecipes,
                    stocksRoute = StocksFragment.naviFromRecipes,
                    profileRoute = ProfileFragment.naviFromRecipes,
                    settingsRoute = SettingsFragment.naviFromRecipes,
                )
            }
        )
    }

    @Composable
    private fun buildAppBar() {
        TopAppBar(
            modifier = Modifier.testTag(RecipesPageTag.AppBar),
            title = { Text(getString(R.string.recipes_title)) },
            actions = {
                IconButton(
                    modifier = Modifier.testTag(RecipesPageTag.LayoutButton),
                    onClick = { showGridLayout.value = !showGridLayout.value },
                ) {
                    Icon(
                        imageVector = if (showGridLayout.value) Icons.Default.GridOff else Icons.Default.GridOn,
                        contentDescription = "Layout button"
                    )
                }
            }
        )
    }

    @Composable
    private fun buildFab() {
        FloatingExpandingButton {
            ExtendedFloatingActionButton(
                modifier = Modifier.testTag(RecipesPageTag.NewRecipeButton),
                text = { Text(text = "Rezept") },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "New recipe FAB",
                    )
                },
                onClick = { viewModel.navigate(NaviEvent.Navigate(R.id.recipes_to_recipe_details)) },
            )
        }
    }

    @Composable
    private fun buildContent(innerPadding: PaddingValues) {
        val modelResp by viewModel.recipesModel.observeAsState(null)
        when {
            modelResp is Result.Error -> ErrorScreen(modelResp!!.message!!.asString())
            modelResp?.data?.isLoading != false -> LoadingIndicator()
            else -> {
                val model = modelResp!!.data!!
                val settings = model.settings!!
                val recipes = model.recipes!!
                val cookableMap = model.cookableMap!!

                Column(
                    verticalArrangement = Arrangement.Top
                ) {
                    if (recipes.isEmpty()) {
                        EmptyListComp(getString(R.string.no_recipes))
                        return
                    }

                    GridListLayout(
                        innerPadding,
                        showGridLayout,
                        recipes.groupByCategory,
                        settings::categoryColor,
                        viewModel::editCategory
                    ) { _, recipe ->
                        val color = settings.categoryColor(recipe)
                        DismissItem(
                            recipe.name,
                            color,
                            onSwipe = { viewModel.deleteRecipe(recipe) },
                            onClick = {
                                viewModel.navigate(
                                    RecipeDetailsFragment.naviFromRecipes(recipe.uuid)
                                )
                            },
                        ) { RecipeItem(recipe, cookableMap[recipe.uuid] ?: false) }
                    }
                }
            }
        }
    }

    @Composable
    private fun RecipeItem(recipe: Recipe, isCookable: Boolean) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .padding(12.dp)
                .testTag(RecipeTag(recipe.name, recipe.category)),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    recipe.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start
                )
                Icon(
                    modifier = Modifier
                        .testTag(if (isCookable) RecipeTag.CookableIconTag else RecipeTag.UncookableIconTag)
                        .background(
                            if (isCookable) BaseColors.Green else BaseColors.Red,
                            shape = CircleShape
                        ),
                    imageVector = Icons.Default.Circle,
                    contentDescription = "cookable indicator"
                )
            }
        }
    }

}