package de.bitb.pantryplaner.ui.items

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
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
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.KEY_CHECKLIST_UUID
import de.bitb.pantryplaner.ui.base.TestTags
import de.bitb.pantryplaner.ui.base.comps.*
import de.bitb.pantryplaner.ui.base.highlightedText
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.comps.AddSubRow
import de.bitb.pantryplaner.ui.comps.SelectItemHeader
import de.bitb.pantryplaner.ui.dialogs.AddItemDialog
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog
import de.bitb.pantryplaner.ui.dialogs.FilterDialog
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

        if (showAddDialog.value) {
            val items by viewModel.itemList.collectAsState(null)
            AddItemDialog(
                categorys = items?.data?.keys?.toList() ?: listOf(),
                onConfirm = { name, category, close ->
                    viewModel.addItem(name, category)
                    if (close) {
                        showAddDialog.value = false
                    }
                },
                onDismiss = { showAddDialog.value = false },
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
                            contentDescription = "Finish FAB",
                        )
                    },
                    onClick = { showAddToDialog.value = true },
                )
            }
        }
    }

    @Composable
    private fun buildContent(innerPadding: PaddingValues) {
        val items by viewModel.itemList.collectAsState(null)
        val categorys = items?.data?.keys?.toList() ?: listOf()
        when {
            items is Resource.Error -> {
                showSnackBar("ERROR".asResString())
                ErrorScreen(items!!.message!!.asString())
            }
            items == null -> LoadingIndicator()
            items?.data?.isEmpty() == true -> EmptyListComp(getString(R.string.no_items))
            else -> GridListLayout(
                innerPadding,
                showGridLayout,
                items!!.data!!,
                { it.color },
                viewModel::editCategory
            ) { listItem(it, categorys) }
        }
    }

    @Composable
    private fun listItem(item: Item, categorys: List<String>) {
        val showEditDialog = remember { mutableStateOf(false) }
        useEditItemDialog(showEditDialog, item, categorys, viewModel::editItem)

        DissmissItem(
            item.name,
            item.color,
            onRemove = { viewModel.removeItem(item) },
            onClick = { viewModel.checkItem(item.uuid) },
            onLongClick = { showEditDialog.value = true },
        ) {
            if (viewModel.isSelectMode) {
                val checkedItems = viewModel.checkedItems.collectAsState()
                SelectItemHeader(
                    item,
                    checkedItems.value.contains(item.uuid),
                    checkItem = viewModel::checkItem
                )
            } else {
                stockItem(item)
            }
        }
    }

    @Composable
    private fun stockItem(item: Item) {
        val filter = viewModel.filterBy.collectAsState(null)
        val text = highlightedText(item.name, filter.value?.searchTerm ?: "")

        Column(
            modifier = Modifier
                .fillMaxWidth(),
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
            AddSubRow(
                item.uuid,
                item.amount,
                errors,
                viewModel::changeItemAmount,
            )
        }
    }
}