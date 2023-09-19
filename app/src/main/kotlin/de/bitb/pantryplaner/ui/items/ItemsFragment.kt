package de.bitb.pantryplaner.ui.items

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.StockItem
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.KEY_CHECKLIST_UUID
import de.bitb.pantryplaner.ui.base.TestTags
import de.bitb.pantryplaner.ui.base.comps.EmptyListComp
import de.bitb.pantryplaner.ui.base.comps.ErrorScreen
import de.bitb.pantryplaner.ui.base.comps.GridListLayout
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator
import de.bitb.pantryplaner.ui.base.comps.SearchBar
import de.bitb.pantryplaner.ui.base.comps.dissmissItem
import de.bitb.pantryplaner.ui.base.comps.onBack
import de.bitb.pantryplaner.ui.base.highlightedText
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.comps.AddSubRow
import de.bitb.pantryplaner.ui.comps.SelectItemHeader
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog
import de.bitb.pantryplaner.ui.dialogs.FilterDialog
import de.bitb.pantryplaner.ui.dialogs.useAddItemDialog
import de.bitb.pantryplaner.ui.dialogs.useEditItemDialog

@AndroidEntryPoint
class ItemsFragment : BaseFragment<ItemsViewModel>() {
    override val viewModel: ItemsViewModel by viewModels()

    private lateinit var showGridLayout: MutableState<Boolean>
    private lateinit var showFilterDialog: MutableState<Boolean>
    private lateinit var showAddDialog: MutableState<Boolean>
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
        showAddDialog = remember { mutableStateOf(false) }
        showAddToDialog = remember { mutableStateOf(false) }
        showSearchBar = remember { mutableStateOf(false) }

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
            topBar = { buildAppBar() },
            content = { buildContent(it) },
            floatingActionButton = { buildFab() }
        )

        if (showFilterDialog.value) {
            FilterDialog(
                viewModel.filterBy.value,
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
    private fun buildAppBar() {
        TopAppBar(
            modifier = Modifier.testTag(TestTags.ItemsPage.AppBar.name),
            title = {
                val isSearching by viewModel.isSearching.collectAsState(false)
                if (showSearchBar.value) SearchBar(
                    showSearchBar,
                    isSearching,
                    viewModel.filterBy.value.searchTerm,
                    viewModel::search,
                )
                else Text(getString(R.string.items_title))
            },
            actions = {
                IconButton(
                    modifier = Modifier.testTag(TestTags.ItemsPage.SearchButton.name),
                    onClick = { showSearchBar.value = !showSearchBar.value },
                ) {
                    Icon(
                        imageVector = searchButtonIcon,
                        contentDescription = "Search button"
                    )
                }

                if (!showSearchBar.value) {
                    IconButton(
                        modifier = Modifier.testTag(TestTags.ItemsPage.LayoutButton.name),
                        onClick = { showGridLayout.value = !showGridLayout.value },
                    ) {
                        Icon(
                            imageVector = if (showGridLayout.value) Icons.Default.GridOff else Icons.Default.GridOn,
                            contentDescription = "Layout button"
                        )
                    }
                    IconButton(
                        modifier = Modifier.testTag(TestTags.ItemsPage.FilterButton.name),
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
                text = { Text(text = "Neu") },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "New item FAB",
                    )
                },
                onClick = { showAddDialog.value = true },
            )
            if (viewModel.isSelectMode) {
                Spacer(modifier = Modifier.height(8.dp))
                ExtendedFloatingActionButton(
                    text = { Text(text = "Hinzufügen") },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add FAB",
                        )
                    },
                    onClick = { showAddToDialog.value = true },
                )
            }
        }
    }

    @Composable
    private fun buildContent(innerPadding: PaddingValues) {
        val stockModel by viewModel.stockModel.observeAsState()
        when {
            stockModel?.data?.isLoading != false -> LoadingIndicator()
            stockModel is Resource.Error -> ErrorScreen(stockModel!!.message!!.asString())
            else -> {
                val model = stockModel!!.data!!
                val stockItems = model.stockItem!!
                val items = model.items
                val categorys = items?.keys?.toList() ?: listOf()
                val users = model.connectedUser ?: listOf()
                useAddItemDialog(
                    showAddDialog,
                    categorys,
                    users,
                ) { stockItem, item, close ->
                    viewModel.addItem(item, stockItem)
                    if (close) showAddDialog.value = false
                }

                if (stockItems.isEmpty()) {
                    EmptyListComp(getString(R.string.no_items))
                } else {
                    GridListLayout(
                        innerPadding,
                        showGridLayout,
                        items!!,
                        { stockItems.values.first().color }, //TODO color?
                        viewModel::editCategory
                    ) { _, item ->
                        listItem(
                            stockItems[item.uuid] ?: item.toStockItem(),
                            item,
                            categorys,
                            users,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun listItem(
        stockItem: StockItem,
        item: Item,
        categorys: List<String>,
        users: List<User>,
    ) {
        val showEditDialog = remember { mutableStateOf(false) }
        useEditItemDialog(
            showEditDialog,
            stockItem,
            item,
            categorys,
            users,
        ) { si, i, _ -> viewModel.editItem(si, i) }

        dissmissItem(
            item.name,
            stockItem.color,
            onSwipe = { viewModel.deleteItem(item, stockItem) },
            onClick = { viewModel.checkItem(item.uuid) },
            onLongClick = { showEditDialog.value = true },
        ) {
            if (viewModel.isSelectMode) {
                val checkedItems = viewModel.checkedItems.collectAsState()
                SelectItemHeader(
                    stockItem,
                    item,
                    checkedItems.value.contains(item.uuid),
                    checkItem = viewModel::checkItem
                )
            } else {
                stockItem(item, stockItem)
            }
        }
    }

    @Composable
    private fun stockItem(item: Item, stockItem: StockItem) {
        val filter = viewModel.filterBy.collectAsState(null)
        val text = highlightedText(item.name, filter.value?.searchTerm ?: "")

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BaseColors.LightGray.copy(alpha = .1f))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Start
            )

            val errors = viewModel.itemErrorList.collectAsState(listOf())
            val color =
                if (errors.value.contains(item.uuid)) BaseColors.FireRed
                else BaseColors.White
            AddSubRow(
                stockItem.amount,
                color,
            ) { viewModel.changeItemAmount(item.uuid, it) }
        }
    }
}