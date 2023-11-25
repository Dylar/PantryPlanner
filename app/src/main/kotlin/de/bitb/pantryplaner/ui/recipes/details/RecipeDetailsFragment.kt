package de.bitb.pantryplaner.ui.recipes.details

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.data.model.Item
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
import de.bitb.pantryplaner.ui.base.comps.buildCategoryDropDown
import de.bitb.pantryplaner.ui.base.comps.buildUserDropDown
import de.bitb.pantryplaner.ui.base.comps.onBack
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.testTags.ItemTag
import de.bitb.pantryplaner.ui.base.testTags.RecipeDetailsPageTag
import de.bitb.pantryplaner.ui.base.testTags.RecipeTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import de.bitb.pantryplaner.ui.comps.AddSubRow
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog
import de.bitb.pantryplaner.ui.select.SelectItemsFragment
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeDetailsFragment : BaseFragment<RecipeViewModel>() {

    companion object {
        const val KEY_RECIPE_UUID = "keyRecipeUUID"
        fun naviFromRecipes(uuid: String): NaviEvent =
            NaviEvent.Navigate(
                R.id.recipes_to_recipe_details,
                bundleOf(KEY_RECIPE_UUID to uuid)
            )
    }

    override val viewModel: RecipeViewModel by viewModels()

    private lateinit var showGridLayout: MutableState<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uuid = arguments?.getString(KEY_RECIPE_UUID)
        viewModel.initRecipe(uuid)

        parentFragmentManager.setFragmentResultListener(
            SelectItemsFragment.REQUEST_ITEMS,
            this
        ) { _, bundle ->
            val result = bundle.getStringArray(SelectItemsFragment.ITEMS_KEY)
            viewModel.addItems(result?.toList().orEmpty())
        }
    }

    @Composable
    override fun screenContent() {
        showGridLayout = remember { mutableStateOf(true) }

        onBack { onDismiss ->
            ConfirmDialog(
                "Änderungen speichern?",
                "Möchten Sie die Änderungen speichern?",
                confirmBtn = "SPEICHERN",
                cancelBtn = "VERWERFEN",
                onConfirm = viewModel::saveRecipe,
                onDismiss = {
                    onDismiss()
                    navController.popBackStack()
                },
            )
        }
        val recipeModel by viewModel.recipeModel.observeAsState(null)
        val isCookable = recipeModel?.data?.isCookable == true
        Scaffold(
            modifier = Modifier.testTag(RecipeDetailsPageTag.RecipeDetailsPage),
            scaffoldState = scaffoldState,
            topBar = { buildAppBar(recipeModel, isCookable) },
            content = { buildContent(it, recipeModel) },
            floatingActionButton = { buildFab(recipeModel, isCookable) },
        )
    }

    @Composable
    private fun buildAppBar(recipeModel: Result<RecipeModel>?, isCookable: Boolean) {
        TopAppBar(
            modifier = Modifier.testTag(RecipeDetailsPageTag.AppBar),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        modifier = Modifier
                            .testTag(if (isCookable) RecipeTag.CookableIconTag else RecipeTag.UncookableIconTag)
                            .background(
                                color = if (isCookable) BaseColors.Green else BaseColors.Red,
                                shape = CircleShape
                            ),
                        imageVector = Icons.Default.Circle,
                        contentDescription = "cookable indicator"
                    )
                    Text(
                        recipeModel?.data?.recipe?.name ?: getString(R.string.loading_text),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1F)
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { showGridLayout.value = !showGridLayout.value },
                    modifier = Modifier.testTag(RecipeDetailsPageTag.LayoutButton)
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
    private fun buildFab(recipeModel: Result<RecipeModel>?, isCookable: Boolean) {
        if (recipeModel?.data?.isLoading != false) return
        FloatingExpandingButton {
            val model = recipeModel.data
            val recipe = model.recipe
            val isOld = recipe?.isNew() == false
            if (isCookable && isOld) {
                var showCookConfirmDialog by remember { mutableStateOf(false) }
                if (showCookConfirmDialog) {
                    ConfirmDialog(
                        "Rezept gekocht?",
                        "Haben Sie das Rezept gekocht und möchten die Items aus ihrem Bestand entfernen?",
                        onConfirm = {
                            showCookConfirmDialog = false
                            viewModel.cookRecipe()
                        },
                        onDismiss = { showCookConfirmDialog = false },
                    )
                }
                ExtendedFloatingActionButton(
                    modifier = Modifier.testTag(RecipeDetailsPageTag.CookButton),
                    onClick = { showCookConfirmDialog = true },
                    text = { Text(text = "Gekocht") },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Restaurant,
                            contentDescription = "Cook it",
                        )
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (isOld) {
                var showSelectChecklistDialog by remember { mutableStateOf(false) }
                if (showSelectChecklistDialog) {
                    //TODO open checklist selection
                    ConfirmDialog(
                        "Kaufen?",
                        "Möchten Sie das Rezept kaufen?",
                        onConfirm = {
                            showSelectChecklistDialog = false
//                            viewModel.shareItem(item)
                        },
                        onDismiss = { showSelectChecklistDialog = false },
                    )
                }
                ExtendedFloatingActionButton(
                    modifier = Modifier.testTag(RecipeDetailsPageTag.ShopButton),
                    onClick = { showSelectChecklistDialog = true },
                    text = { Text(text = "Kaufen") },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.ShoppingCart,
                            contentDescription = "add to checklist",
                        )
                    },
                )
            }
            if (model.isCreator()) {
                Spacer(modifier = Modifier.height(8.dp))
                ExtendedFloatingActionButton(
                    modifier = Modifier.testTag(RecipeDetailsPageTag.AddItemButton),
                    onClick = {
                        lifecycleScope.launch {
                            viewModel.navigate(SelectItemsFragment.naviFromRecipeDetails)
                        }
                    },
                    text = { Text(text = "Items") },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "add Items",
                        )
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExtendedFloatingActionButton(
                    modifier = Modifier.testTag(RecipeDetailsPageTag.SaveRecipeButton),
                    onClick = viewModel::saveRecipe,
                    text = { Text(text = "Speichern") },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "save Recipe",
                        )
                    },
                )
            }
        }
    }

    @Composable
    private fun buildContent(innerPadding: PaddingValues, recipeModel: Result<RecipeModel>?) {
        when {
            recipeModel is Result.Error -> ErrorScreen(recipeModel.message!!.asString())
            recipeModel?.data?.isLoading != false -> LoadingIndicator()
            else -> Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
            ) {
                val model = recipeModel.data
                val settings = model.settings!!
                val recipe = model.recipe!!
                val items = model.items!!
                // TODO stock tabs to check items available on each stock
                RecipeDetails(model)
                if (items.isEmpty()) {
                    EmptyListComp(getString(R.string.no_items))
                } else {
                    GridListLayout(
                        innerPadding,
                        showGridLayout,
                        items.groupByCategory,
                        settings::categoryColor,
                    ) { _, item ->
                        val color = settings.categoryColor(item)
                        RecipeListItem(
                            recipe,
                            item,
                            model.isSharedWith(item),
                            color,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun RecipeDetails(model: RecipeModel) {
        val isCreator = model.isCreator()

        buildUserDropDown(
            "Rezept wird nicht geteilt",
            model.connectedUser!!,
            remember { mutableStateOf(model.sharedUser!!) },
            canChange = isCreator
        ) {
            viewModel.setSharedWith(it)
        }

        OutlinedTextField(
            readOnly = !isCreator,
            modifier = Modifier
                .testTag(RecipeDetailsPageTag.RecipeName)
                .padding(4.dp)
                .fillMaxWidth(),
            singleLine = true,
            label = { Text(stringResource(R.string.item_name)) },
            value = viewModel.recipeName,
            onValueChange = { viewModel.setName(it) },
        )

        buildCategoryDropDown(
            viewModel.recipeCategory,
            model.items?.map { it.category }.orEmpty(),
            canChange = isCreator
        ) {
            viewModel.setCategory(it)
        }
    }

    @Composable
    private fun RecipeListItem(
        recipe: Recipe,
        item: Item,
        isShared: Boolean,
        color: Color,
    ) {
        var showActionDialog by remember { mutableStateOf(false) }
        if (showActionDialog) {
            ConfirmDialog(
                "Item hinzufügen",
                "Möchten Sie das Item ihrem Item-Pool hinzufügen?",
                onConfirm = {
                    showActionDialog = false
                    viewModel.shareItem(item)
                },
                onDismiss = { showActionDialog = false },
            )
        }

        val recipeItem = recipe.items.first { it.uuid == item.uuid }
        DismissItem(
            item.name,
            color,
            onSwipe = { viewModel.removeItem(item) },
            onLongClick = { showActionDialog = !isShared },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ItemTag(item.name, item.category)),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        item.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start
                    )
                    if (!isShared)
                        Icon(
                            Icons.Filled.LinkOff,
                            modifier = Modifier
                                .testTag(ItemTag.UnsharedIconTag)
                                .padding(4.dp)
                                .size(18.dp),
                            contentDescription = null,
                        )
                }

                AddSubRow(recipeItem.amount) { viewModel.changeItemAmount(item.uuid, it) }
            }
        }
    }
}