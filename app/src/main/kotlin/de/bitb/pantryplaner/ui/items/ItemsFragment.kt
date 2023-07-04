package de.bitb.pantryplaner.ui.items

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import de.bitb.pantryplaner.ui.base.comps.*
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.comps.AddSubRow
import de.bitb.pantryplaner.ui.comps.SelectItemHeader
import de.bitb.pantryplaner.ui.dialogs.AddItemDialog
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog
import de.bitb.pantryplaner.ui.dialogs.FilterDialog
import de.bitb.pantryplaner.ui.dialogs.useEditItemDialog

@AndroidEntryPoint
class ItemsFragment : BaseFragment<ItemsViewModel>() {
    companion object {
        const val APPBAR_TAG = "ItemAppbar"
        const val LAYOUT_BUTTON_TAG = "ItemLayoutButton"
        const val FILTER_BUTTON_TAG = "ItemFilterButton"
        const val ADD_BUTTON_TAG = "ItemAddButton"
        const val LIST_TAG = "CheckList"
        const val GRID_TAG = "CheckGrid"
    }

    override val viewModel: ItemsViewModel by viewModels()

    private lateinit var showGridLayout: MutableState<Boolean>
    private lateinit var showFilterDialog: MutableState<Boolean>
    private lateinit var showAddDialog: MutableState<Boolean>
    private lateinit var showAddToDialog: MutableState<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initItems(arguments?.getString(KEY_CHECKLIST_UUID))
    }

    @Composable
    override fun ScreenContent() {
        showGridLayout = remember { mutableStateOf(true) }
        showFilterDialog = remember { mutableStateOf(false) }
        showAddDialog = remember { mutableStateOf(false) }
        showAddToDialog = remember { mutableStateOf(false) }

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
                viewModel.filterBy,
                onConfirm = { showFilterDialog.value = false },
                onDismiss = { showFilterDialog.value = false },
            )
        }

        if (showAddDialog.value) {
            val items by viewModel.itemList.collectAsState(null)
            AddItemDialog(
                categorys = items?.data?.keys?.toList() ?: listOf(),
                onConfirm = { name, category, color, close ->
                    viewModel.addItem(name, category, color)
                    if (close) {
                        showAddDialog.value = false
                    }
                },
                onDismiss = { showAddDialog.value = false },
            )
        }

        if (showAddToDialog.value) {
            ConfirmDialog(
                // TODO select checklist dialog
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
            modifier = Modifier.testTag(APPBAR_TAG),
            title = { Text(getString(R.string.items_title)) },
            actions = {
                IconButton(
                    modifier = Modifier.testTag(LAYOUT_BUTTON_TAG),
                    onClick = { showGridLayout.value = !showGridLayout.value },
                ) {
                    Icon(
                        imageVector = if (showGridLayout.value) Icons.Default.GridOff else Icons.Default.GridOn,
                        contentDescription = "Layout button"
                    )
                }
                IconButton(
                    modifier = Modifier.testTag(FILTER_BUTTON_TAG),
                    onClick = { showFilterDialog.value = !showFilterDialog.value },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = "Filter button"
                    )
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
            if (viewModel.isSelectModus) {
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
            ) { ListItem(it) }
        }
    }

    @Composable
    fun ListItem(item: Item) {
        val showEditDialog = remember { mutableStateOf(false) }
        useEditItemDialog(showEditDialog, item, viewModel::editItem)

        val showRemoveDialog = remember { mutableStateOf(false) }
        DissmissItem(
            item.name,
            item.color,
            showRemoveDialog,
            onRemove = { viewModel.removeItem(item) },
            onClick = { viewModel.checkItem(item.uuid) },
            onLongClick = { showEditDialog.value = true },
        )
        {
            if (viewModel.isSelectModus) {
                val checkedItems = viewModel.checkedItems.collectAsState()
                SelectItemHeader(
                    item,
                    checkedItems.value.contains(item.uuid),
                    false,
                    viewModel::checkItem
                )
            } else {
                StockItem(item)
            }
        }
    }

    @Composable
    fun StockItem(item: Item) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                item.name,
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