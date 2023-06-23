package de.bitb.pantryplaner.ui.checklist

import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.KEY_CHECKLIST_UUID
import de.bitb.pantryplaner.ui.base.composable.*
import de.bitb.pantryplaner.ui.base.naviChecklistToItems
import de.bitb.pantryplaner.ui.dialogs.FilterDialog
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog
import de.bitb.pantryplaner.ui.dialogs.EditCategoryDialog
import de.bitb.pantryplaner.ui.dialogs.EditItemDialog

@AndroidEntryPoint
class ChecklistFragment : BaseFragment<ChecklistViewModel>() {
    companion object {
        const val APPBAR_TAG = "CheckAppbar"
        const val LAYOUT_BUTTON_TAG = "CheckLayoutButton"
        const val FILTER_BUTTON_TAG = "CheckFilterButton"
        const val ADD_BUTTON_TAG = "CheckAddButton"
        const val UNCHECK_BUTTON_TAG = "CheckUncheckButton"
        const val LIST_TAG = "CheckList"
        const val GRID_TAG = "CheckGrid"
    }

    override val viewModel: ChecklistViewModel by viewModels()

    private lateinit var showGridLayout: MutableState<Boolean>
    private lateinit var showFilterDialog: MutableState<Boolean>
    private lateinit var showFinishDialog: MutableState<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uuid = arguments?.getString(KEY_CHECKLIST_UUID) ?: throw Exception()
        viewModel.initChecklist(uuid)
    }

    @Composable
    override fun ScreenContent() {
        showGridLayout = remember { mutableStateOf(true) }
        showFilterDialog = remember { mutableStateOf(false) }
        showFinishDialog = remember { mutableStateOf(false) }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            content = { buildContent(it) },
            bottomBar = { buildBottomBar() },
        )

        if (showFilterDialog.value) {
            FilterDialog(
                viewModel.filterBy,
                onConfirm = { showFilterDialog.value = false },
                onDismiss = { showFilterDialog.value = false },
            )
        }

        if (showFinishDialog.value) {
            ConfirmDialog(
                "Fertig?", // TODO add to stock?
                "Möchten Sie die Checklist erledigen und die Items ihrem Bestand hinzufügen?",
                onConfirm = {
                    viewModel.finishChecklist()
                    showFinishDialog.value = false
                },
                onDismiss = { showFinishDialog.value = false },
            )
        }
    }

    @Composable
    private fun buildAppBar() {
        val checklist by viewModel.checkList.collectAsState(null)
        TopAppBar(
            modifier = Modifier.testTag(APPBAR_TAG),
            title = {
                Text(
                    getString(
                        R.string.check_title,
                        checklist?.data?.name ?: "Loading..."
                    )
                )
            },
            actions = {
                IconButton(
                    onClick = { showGridLayout.value = !showGridLayout.value },
                    modifier = Modifier.testTag(LAYOUT_BUTTON_TAG)
                ) {
                    Icon(
                        imageVector = if (showGridLayout.value) Icons.Default.GridOff else Icons.Default.GridOn,
                        contentDescription = "Layout button"
                    )
                }
                IconButton(
                    onClick = { showFilterDialog.value = !showFilterDialog.value },
                    modifier = Modifier.testTag(FILTER_BUTTON_TAG)
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
    private fun buildBottomBar() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(2f)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .testTag(UNCHECK_BUTTON_TAG),
                    onClick = { showFinishDialog.value = true },
                    content = { Text("Erledigen") }
                )
            }
            Box(
                modifier = Modifier.padding(8.dp)
            ) {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .testTag(ADD_BUTTON_TAG),
                    onClick = { naviChecklistToItems(viewModel.checkListId) }
                ) { Icon(Icons.Filled.Add, contentDescription = "Add Item") }
            }
        }
    }

    @Composable
    private fun buildContent(innerPadding: PaddingValues) {
        val items by viewModel.itemMap.collectAsState(null)
        when {
            items is Resource.Error -> {
                showSnackBar("ERROR".asResString())
                ErrorScreen(items!!.message!!.asString())
            }
            items == null -> LoadingIndicator()
            items?.data?.isEmpty() == true -> EmptyListComp(getString(R.string.no_items))
            else -> CheckList(innerPadding, items!!.data!!)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun CheckList(
        innerPadding: PaddingValues,
        items: Map<String, List<Item>>
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(innerPadding)
        ) {
            if (showGridLayout.value) {
                LazyVerticalGrid(
                    GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(GRID_TAG),
                    verticalArrangement = Arrangement.Top,
                    horizontalArrangement = Arrangement.Center,
                    contentPadding = PaddingValues(4.dp),
                ) {
                    items.forEach { (headerText, list) ->
                        if (headerText.isNotBlank()) {
                            stickyGridHeader { Header(headerText) }
                        }
                        items(list.size) { CheckListItem(list[it]) }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(LIST_TAG),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(4.dp),
                ) {
                    items.forEach { (headerText, list) ->
                        if (headerText.isNotBlank()) {
                            stickyHeader { Header(headerText) }
                        }
                        items(list.size) { CheckListItem(list[it]) }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun Header(category: String) {
        var showEditDialog by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.combinedClickable(
                    onClick = {}, // required? Oo
                    onLongClick = { showEditDialog = true }
                )
            ) {
                Text(
                    category,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        if (showEditDialog) {
            EditCategoryDialog(
                category,
                onConfirm = {
                    viewModel.editCategory(category, it)
                    showEditDialog = false
                },
                onDismiss = { showEditDialog = false },
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun CheckListItem(item: Item) {
        var showRemoveDialog by remember { mutableStateOf(false) }
        var showEditDialog by remember { mutableStateOf(false) }
        val dismissState = rememberDismissState(
            confirmStateChange = {
                if (it == DismissValue.DismissedToEnd) {
                    showRemoveDialog = true
                    true
                } else false
            }
        )

        LaunchedEffect(dismissState.currentValue) {
            if (dismissState.currentValue != DismissValue.Default) {
                dismissState.reset()
            }
        }

        if (showRemoveDialog) {
            ConfirmDialog(
                "Remove Item",
                "Möchten Sie folgendes Item entfernen?\n${item.name}",
                onConfirm = {
                    viewModel.removeItem(item)
                    showRemoveDialog = false
                },
                onDismiss = { showRemoveDialog = false },
            )
        }

        if (showEditDialog) {
            EditItemDialog(
                item = item,
                onConfirm = { name, category, color ->
                    viewModel.editItem(item, name, category, color)
                    showEditDialog = false
                },
                onDismiss = { showEditDialog = false },
            )
        }

        val checklist by viewModel.checkList.collectAsState(null)
        val isChecked = checklist?.data?.checked?.contains(item.uuid) == true
        SwipeToDismiss(
            modifier = Modifier.padding(2.dp),
            state = dismissState,
            directions = setOf(DismissDirection.StartToEnd),
            background = { DeleteItemBackground() },
            dismissContent = {
                Card(
                    elevation = 4.dp,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .combinedClickable(
                            onClick = { viewModel.checkItem(item) },
                            onLongClick = { showEditDialog = true },
                        ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            isChecked,
                            modifier = Modifier
                                .weight(.2f),
                            onCheckedChange = { viewModel.checkItem(item) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = item.color,
                                uncheckedColor = item.color
                            )
                        )
                        Column(
                            modifier = Modifier
                                .padding(start = 2.dp)
                                .weight(.7f)
                        )
                        {
                            if (item.category.isNotBlank()) {
                                Text(
                                    item.category,
                                    fontSize = 10.sp,
                                )
                            }
                            Text(
                                item.name,
                                modifier = Modifier,
                                textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
                }
            }
        )
    }
}