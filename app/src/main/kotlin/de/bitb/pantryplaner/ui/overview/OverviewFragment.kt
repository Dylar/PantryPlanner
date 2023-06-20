package de.bitb.pantryplaner.ui.overview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.composable.ErrorScreen
import de.bitb.pantryplaner.ui.base.composable.LoadingIndicator
import de.bitb.pantryplaner.ui.base.composable.asResString
import de.bitb.pantryplaner.ui.base.composable.stickyGridHeader
import de.bitb.pantryplaner.ui.base.naviToChecklist
import de.bitb.pantryplaner.ui.base.naviToItems
import de.bitb.pantryplaner.ui.base.naviToReleaseNotes
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.checklist.ChecklistFragment
import de.bitb.pantryplaner.ui.dialogs.AddChecklistDialog
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog
import de.bitb.pantryplaner.ui.dialogs.InfoDialog

@AndroidEntryPoint
class OverviewFragment : BaseFragment<OverviewViewModel>() {
    companion object {
        const val APPBAR_TAG = "OverviewAppbar"
        const val INFO_BUTTON_TAG = "OverviewInfoButton"
        const val LAYOUT_BUTTON_TAG = "OverviewLayoutButton"
        const val ADD_BUTTON_TAG = "OverviewAddButton"
        const val GRID_TAG = "OverviewGrid"
        const val LIST_TAG = "OverviewList"
    }

    override val viewModel: OverviewViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
        var showGridLayout by remember { mutableStateOf(true) }
        var showInfoDialog by remember { mutableStateOf(false) }
        var showAddDialog by remember { mutableStateOf(false) }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text(getString(R.string.overview_title)) },
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
                                .testTag(ChecklistFragment.UNCHECK_BUTTON_TAG),
                            onClick = ::naviToItems,
                            content = { Text("Zu Items") }
                        )
                    }
                    Box(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        FloatingActionButton( // TODO open multi items -> add template, checklist, item?
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .testTag(ADD_BUTTON_TAG),
                            onClick = { showAddDialog = true }
                        ) { Icon(Icons.Filled.Add, contentDescription = "Add Menu") }
                    }
                }
            }
        ) { innerPadding ->
            val checklist by viewModel.checkList.collectAsState(null)
            if (checklist is Resource.Error) {
                showSnackBar("ERROR".asResString())
                ErrorScreen(checklist!!.message!!.asString())
            } else {
                CheckList(innerPadding, showGridLayout, checklist?.data)
            }
        }

        if (showInfoDialog) {
            InfoDialog(naviToReleaseNotes = ::naviToReleaseNotes) { showInfoDialog = false }
        }

        if (showAddDialog) {
            AddChecklistDialog(
                onConfirm = { name ->
                    viewModel.addChecklist(name)
                    showAddDialog = false
                },
                onDismiss = { showAddDialog = false },
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun CheckList(
        innerPadding: PaddingValues,
        showGridLayout: Boolean,
        checklists: List<Checklist>?
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(innerPadding)
        ) {
            when {
                checklists == null -> LoadingIndicator()
                checklists.isEmpty() -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text(
                        modifier = Modifier.fillMaxSize(),
                        text = getString(R.string.no_checklists),
                        textAlign = TextAlign.Center,
                    )
                }
                else -> {
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
                            // TODO liste über header einklappbar machen
                            checklists.groupBy { it.finished }.forEach { (isFinished, list) ->
                                stickyGridHeader { Header(if (isFinished) "Finished" else "Checklist") }
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
                            // TODO liste über header einklappbar machen
                            checklists.groupBy { it.finished }.forEach { (isFinished, list) ->
                                stickyHeader { Header(if (isFinished) "Finished" else "Checklist") }
                                items(list.size) { CheckListItem(list[it]) }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Header(category: String) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Card {
                Text(
                    category,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun CheckListItem(checklist: Checklist) {
        var showRemoveDialog by remember { mutableStateOf(false) }
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
                "Remove Checklist",
                "Möchtest du folgende Checklist entfernen?\n${checklist.name}",
                onConfirm = {
                    viewModel.removeChecklist(checklist)
                    showRemoveDialog = false
                },
                onDismiss = { showRemoveDialog = false },
            )
        }

        SwipeToDismiss(
            modifier = Modifier
                .height(48.dp)
                .padding(2.dp),
            state = dismissState,
            directions = setOf(DismissDirection.StartToEnd),
            background = {
                Card(
                    elevation = 4.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 4.dp)
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
                        .fillMaxSize()
                        .padding(vertical = 4.dp)
                        .clickable { naviToChecklist(checklist.uuid) },
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .weight(.7f),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Text(
                            checklist.name,
                            modifier = Modifier,
                            fontSize = 16.sp,
                            textDecoration = if (checklist.finished) TextDecoration.LineThrough else TextDecoration.None
                        )
                    }
                }
            }
        )
    }
}