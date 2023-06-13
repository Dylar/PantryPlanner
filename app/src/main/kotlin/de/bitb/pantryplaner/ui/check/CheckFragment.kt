package de.bitb.pantryplaner.ui.check

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import de.bitb.pantryplaner.ui.base.composable.ErrorScreen
import de.bitb.pantryplaner.ui.base.composable.LoadingIndicator
import de.bitb.pantryplaner.ui.base.composable.asResString
import de.bitb.pantryplaner.ui.base.composable.stickyGridHeader
import de.bitb.pantryplaner.ui.base.naviToReleaseNotes
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.styles.BaseColors.FilterColors
import de.bitb.pantryplaner.ui.dialogs.*

@AndroidEntryPoint
class CheckFragment : BaseFragment<CheckViewModel>() {
    companion object {
        const val APPBAR_TAG = "CheckAppbar"
        const val INFO_BUTTON_TAG = "CheckInfoButton"
        const val LAYOUT_BUTTON_TAG = "CheckLayoutButton"
        const val FILTER_BUTTON_TAG = "CheckFilterButton"
        const val ADD_BUTTON_TAG = "CheckAddButton"
        const val UNCHECK_BUTTON_TAG = "CheckUncheckButton"
        const val LIST_TAG = "CheckList"
        const val GRID_TAG = "CheckGrid"
    }

    override val viewModel: CheckViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
        var showGridLayout by remember { mutableStateOf(true) }
        var showFilterDialog by remember { mutableStateOf(false) }
        val filterBy = remember { mutableStateOf(FilterColors.first()) }
        var showInfoDialog by remember { mutableStateOf(false) }
        var showAddDialog by remember { mutableStateOf(false) }
        var showUncheckDialog by remember { mutableStateOf(false) }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text(getString(R.string.check_title)) },
                    actions = {
                        IconButton(
                            onClick = { showInfoDialog = true },
                            modifier = Modifier.testTag(INFO_BUTTON_TAG)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info dialog"
                            )
                        }
                        IconButton(
                            onClick = { showGridLayout = !showGridLayout },
                            modifier = Modifier.testTag(LAYOUT_BUTTON_TAG)
                        ) {
                            Icon(
                                imageVector = if (showGridLayout) Icons.Default.GridOff else Icons.Default.GridOn,
                                contentDescription = "Layout button"
                            )
                        }
                        IconButton(
                            onClick = { showFilterDialog = !showFilterDialog },
                            modifier = Modifier.testTag(FILTER_BUTTON_TAG)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FilterList,
                                contentDescription = "Filter button"
                            )
                        }

                    }
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
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
                            onClick = { showUncheckDialog = true },
                            content = { Text("Haken entfernen") }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        FloatingActionButton(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .testTag(ADD_BUTTON_TAG),
                            onClick = { showAddDialog = true }
                        ) { Icon(Icons.Filled.Add, contentDescription = "Add Item") }
                    }
                }
            }
        ) { innerPadding ->
            val list by viewModel.checkList.collectAsState(null)
            if (list is Resource.Error) {
                showSnackBar("ERROR".asResString())
                ErrorScreen(list!!.message!!.asString())
            } else {
                var items = list?.data
                if (list?.hasData == true && filterBy.value != FilterColors.first()) {
                    items = items?.filter { it.color == filterBy.value }
                }
                CheckList(innerPadding, showGridLayout, items)
            }
        }

        if (showInfoDialog) {
            InfoDialog(naviToReleaseNotes = ::naviToReleaseNotes) { showInfoDialog = false }
        }

        if (showFilterDialog) {
            fun onDismiss() {
                showFilterDialog = false
            }
            ColorPickerDialog(
                filterBy,
                onConfirm = ::onDismiss,
                onDismiss = ::onDismiss,
            )
        }

        if (showAddDialog) {
            AddDialog(
                onConfirm = { name, category, color, close ->
                    viewModel.addItem(name, category, color)
                    if (close) {
                        showAddDialog = false
                    }
                },
                onDismiss = { showAddDialog = false },
            )
        }

        if (showUncheckDialog) {
            ConfirmDialog(
                "Haken entfernen?",
                "Möchten Sie alle Haken entfernen?",
                onConfirm = {
                    viewModel.uncheckAllItems(filterBy.value)
                    showUncheckDialog = false
                },
                onDismiss = { showUncheckDialog = false },
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun CheckList(innerPadding: PaddingValues, showGridLayout: Boolean, check: List<Item>?) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(innerPadding)
        ) {
            when {
                check == null -> LoadingIndicator()
                check.isEmpty() -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text(
                        modifier = Modifier.fillMaxSize(),
                        text = getString(R.string.no_check),
                        textAlign = TextAlign.Center,
                    )
                }
                else -> {
                    val groupedItems = check.groupBy { it.category }
                        .toSortedMap { a1, a2 -> a1.compareTo(a2) }
                    if (showGridLayout) {
                        LazyVerticalGrid(
                            GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag(GRID_TAG),
                            verticalArrangement = Arrangement.Top,
                            horizontalArrangement = Arrangement.Center,
                            contentPadding = PaddingValues(4.dp),
                        ) {
                            groupedItems.forEach { (headerText, list) ->
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
                            groupedItems.forEach { (headerText, list) ->
                                if (headerText.isNotBlank()) {
                                    stickyHeader { Header(headerText) }
                                }
                                items(list.size) { CheckListItem(list[it]) }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Header(initial: String) {
        Text(
            initial,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
                .padding(start = 60.dp),
            textDecoration = TextDecoration.Underline
        )
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
                "Möchtest du folgendes Item entfernen?\n${item.name}",
                onConfirm = {
                    viewModel.removeItem(item)
                    showRemoveDialog = false
                },
                onDismiss = { showRemoveDialog = false },
            )
        }

        if (showEditDialog) {
            EditDialog(
                item = item,
                onConfirm = { category, color ->
                    viewModel.editItem(item, category, color)
                    showEditDialog = false
                },
                onDismiss = { showEditDialog = false },
            )
        }

        SwipeToDismiss(
            modifier = Modifier.padding(2.dp),
            state = dismissState,
            directions = setOf(DismissDirection.StartToEnd),
            background = {
                Card(
                    elevation = 4.dp,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.checkItem(item) }
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BaseColors.FireRed)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Delete",
                            fontSize = 20.sp,
                            color = BaseColors.White
                        )
                    }
                }
            },
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
                            item.checked,
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
                                    modifier = Modifier,
                                    fontSize = 10.sp,
                                )
                            }
                            Text(
                                item.name,
                                modifier = Modifier,
                                fontSize = 16.sp,
                                textDecoration = if (item.checked) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
                }
            }
        )
    }
}