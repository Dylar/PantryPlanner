package de.bitb.pantryplaner.ui.checklist

import android.os.Bundle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.KEY_CHECKLIST_UUID
import de.bitb.pantryplaner.ui.base.comps.EmptyListComp
import de.bitb.pantryplaner.ui.base.comps.ErrorScreen
import de.bitb.pantryplaner.ui.base.comps.GridListLayout
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator
import de.bitb.pantryplaner.ui.base.comps.SearchBar
import de.bitb.pantryplaner.ui.base.comps.onBack
import de.bitb.pantryplaner.ui.base.testTags.ItemTag
import de.bitb.pantryplaner.ui.base.testTags.SelectItemsPageTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import de.bitb.pantryplaner.ui.comps.SelectItemHeader
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog
import de.bitb.pantryplaner.ui.dialogs.FilterDialog

@AndroidEntryPoint
class SelectItemsFragment : BaseFragment<SelectItemsViewModel>() {
    override val viewModel: SelectItemsViewModel by viewModels()

    private lateinit var showGridLayout: MutableState<Boolean>
    private lateinit var showFilterDialog: MutableState<Boolean>
    private lateinit var showAddToDialog: MutableState<Boolean>
    private lateinit var showSearchBar: MutableState<Boolean>

    private val searchButtonIcon: ImageVector
        get() =
            if (showSearchBar.value) Icons.Default.Cancel
            else if (viewModel.filterBy.value.filterByTerm) Icons.Default.SavedSearch
            else Icons.Default.Search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initItems(arguments?.getString(KEY_CHECKLIST_UUID))
    }

    @Composable
    override fun screenContent() {
        showGridLayout = remember { mutableStateOf(true) }
        showFilterDialog = remember { mutableStateOf(false) }
        showAddToDialog = remember { mutableStateOf(false) }
        showSearchBar = remember { mutableStateOf(false) }

        val filter by viewModel.filterBy.collectAsState(Filter())
        onBack { onDismiss ->
            ConfirmDialog(
                "Discard changes?",
                "Möchten Sie die Item Auswahl verwerfen?",
                onConfirm = { navController.popBackStack() },
                onDismiss = { onDismiss() },
            )
        }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar(filter) },
            content = { buildContent(it) },
            floatingActionButton = { buildFab() }
        )

        if (showFilterDialog.value) {
            FilterDialog(
                filter,
                onConfirm = {
                    viewModel.filterBy.value = it
                    showFilterDialog.value = false
                },
                onDismiss = { showFilterDialog.value = false },
            )
        }

        if (showAddToDialog.value) {
            ConfirmDialog(
                "Hinzufügen?",
                "Möchten Sie alle markierten Items der Checklist hinzufügen?",
                onConfirm = {
                    viewModel.addToChecklist()
                    showAddToDialog.value = false
                },
                onDismiss = { showAddToDialog.value = false },
            )
        }
    }

    @Composable
    private fun buildAppBar(filter: Filter) {
        TopAppBar(
            modifier = Modifier.testTag(SelectItemsPageTag.AppBar),
            title = {
                val isSearching by viewModel.isSearching.collectAsState(false)
                if (showSearchBar.value) SearchBar(
                    showSearchBar,
                    isSearching,
                    filter.searchTerm,
                    viewModel::search,
                )
                else Text(getString(R.string.stock_title))
            },
            actions = {
                IconButton(
                    modifier = Modifier.testTag(SelectItemsPageTag.SearchButton),
                    onClick = { showSearchBar.value = !showSearchBar.value },
                ) {
                    Icon(
                        imageVector = searchButtonIcon,
                        contentDescription = "Search button"
                    )
                }

                if (!showSearchBar.value) {
                    IconButton(
                        modifier = Modifier.testTag(SelectItemsPageTag.LayoutButton),
                        onClick = { showGridLayout.value = !showGridLayout.value },
                    ) {
                        Icon(
                            imageVector = if (showGridLayout.value) Icons.Default.GridOff else Icons.Default.GridOn,
                            contentDescription = "Layout button"
                        )
                    }
                    IconButton(
                        modifier = Modifier.testTag(SelectItemsPageTag.FilterButton),
                        onClick = { showFilterDialog.value = !showFilterDialog.value },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Filter button"
                        )
                    }
                }
            }
        )
    }

    @Composable
    private fun buildFab() {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
        ) {
            ExtendedFloatingActionButton(
                modifier = Modifier.testTag(SelectItemsPageTag.AddSelectionButton),
                text = { Text(text = "Hinzufügen") },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add items FAB",
                    )
                },
                onClick = { showAddToDialog.value = true },
            )
        }
    }

    @Composable
    private fun buildContent(innerPadding: PaddingValues) {
        val itemsModel by viewModel.itemsModel.observeAsState()
        when {
            itemsModel is Result.Error -> ErrorScreen(itemsModel!!.message!!.asString())
            itemsModel?.data?.isLoading != false -> LoadingIndicator()
            else -> {
                val model = itemsModel!!.data!!
                val settings = model.settings!!
                val items = model.items

                if (items?.isEmpty() != false) {
                    EmptyListComp(getString(R.string.no_items))
                } else {
                    GridListLayout(
                        innerPadding,
                        showGridLayout,
                        items,
                        settings::categoryColor,
                    ) { _, item ->
                        val color = settings.categoryColor(item)
                        listItem(item, color)
                    }
                }
            }
        }
    }

    @Composable
    private fun listItem(item: Item, color: Color) {
        Card(
            elevation = 4.dp,
            border = BorderStroke(2.dp, color),
            modifier = Modifier
                .testTag(ItemTag(item.category, item.name))
                .fillMaxWidth()
                .clickable { viewModel.checkItem(item.uuid) }
        ) {
            val checkedItems = viewModel.checkedItems.collectAsState()
            SelectItemHeader(
                item,
                checkedItems.value.contains(item.uuid),
                color = color,
                checkItem = viewModel::checkItem,
            )
        }
    }
}