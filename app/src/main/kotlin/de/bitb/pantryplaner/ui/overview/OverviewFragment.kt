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
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.composable.*
import de.bitb.pantryplaner.ui.base.naviOverviewToItems
import de.bitb.pantryplaner.ui.base.naviToChecklist
import de.bitb.pantryplaner.ui.base.naviToReleaseNotes
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

    private lateinit var showGridLayout: MutableState<Boolean>
    private lateinit var showInfoDialog: MutableState<Boolean>
    private lateinit var showAddDialog: MutableState<Boolean>
    private lateinit var showUnfinishDialog: MutableState<Boolean>


    @Composable
    override fun ScreenContent() {
        showGridLayout = remember { mutableStateOf(true) }
        showInfoDialog = remember { mutableStateOf(false) }
        showAddDialog = remember { mutableStateOf(false) }
        showUnfinishDialog = remember { mutableStateOf(false) }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            content = { buildContent(it) },
            bottomBar = { buildBottomBar() },
        )

        if (showInfoDialog.value) {
            InfoDialog(naviToReleaseNotes = ::naviToReleaseNotes) { showInfoDialog.value = false }
        }

        if (showAddDialog.value) {
            AddChecklistDialog(
                onConfirm = { name ->
                    viewModel.addChecklist(name)
                    showAddDialog.value = false
                },
                onDismiss = { showAddDialog.value = false },
            )
        }
    }

    @Composable
    private fun buildAppBar() {
        TopAppBar(
            modifier = Modifier.testTag(APPBAR_TAG),
            title = { Text(getString(R.string.overview_title)) },
            actions = {
                IconButton(
                    onClick = { showInfoDialog.value = true },
                    modifier = Modifier.testTag(INFO_BUTTON_TAG)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info dialog"
                    )
                }
                IconButton(
                    onClick = { showGridLayout.value = !showGridLayout.value },
                    modifier = Modifier.testTag(LAYOUT_BUTTON_TAG)
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
                        .testTag(ChecklistFragment.UNCHECK_BUTTON_TAG),
                    onClick = ::naviOverviewToItems,
                    content = { Text("Zum Bestand") }
                )
            }
            Box(
                modifier = Modifier.padding(8.dp)
            ) {
                FloatingActionButton( // TODO open multi adding -> add template or checklist -> no everything is a checklist
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .testTag(ADD_BUTTON_TAG),
                    onClick = { showAddDialog.value = true }
                ) { Icon(Icons.Filled.Add, contentDescription = "Add Menu") }
            }
        }
    }

    @Composable
    private fun buildContent(innerPadding: PaddingValues) {
        val checklists by viewModel.checkList.collectAsState(null)
        when {
            checklists is Resource.Error -> {
                showSnackBar("ERROR".asResString())
                ErrorScreen(checklists!!.message!!.asString())
            }
            checklists == null -> LoadingIndicator()
            checklists?.data?.isEmpty() == true -> EmptyListComp(getString(R.string.no_checklists))
            else -> Checklists(innerPadding, checklists!!.data!!)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Checklists(
        innerPadding: PaddingValues,
        checklistsMap: Map<Boolean, List<Checklist>>
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
                    // TODO liste über header einklappbar machen
                    checklistsMap.forEach { (isFinished, list) ->
                        stickyGridHeader { Header(if (isFinished) "Erledigt" else "Checklist") }
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
                    checklistsMap.forEach { (isFinished, list) ->
                        stickyHeader { Header(if (isFinished) "Erledigt" else "Checklist") }
                        items(list.size) { CheckListItem(list[it]) }
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
                "Möchten Sie folgende Checklist entfernen?\n${checklist.name}",
                onConfirm = {
                    viewModel.removeChecklist(checklist)
                    showRemoveDialog = false
                },
                onDismiss = { showRemoveDialog = false },
            )
        }

        if (showUnfinishDialog.value) {
            ConfirmDialog(
                "Checklist öffnen?",
                "Checkliste ist schon erledigt, möchtest Sie sie wieder öffnen und die Items aus deinem Bestand nehmen?",
                onConfirm = {
                    viewModel.unfinishChecklist(checklist)
                    showUnfinishDialog.value = false
                },
                onDismiss = { showUnfinishDialog.value = false },
            )
        }

        SwipeToDismiss(
            modifier = Modifier
                .height(60.dp)
                .padding(2.dp),
            state = dismissState,
            directions = setOf(DismissDirection.StartToEnd),
            background = { DeleteItemBackground() },
            dismissContent = {
                Card(
                    //TODO show item count
                    elevation = 4.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 4.dp)
                        .clickable {
                            if (checklist.finished) {
                                showUnfinishDialog.value = true
                            } else {
                                naviToChecklist(checklist.uuid)
                            }
                        },
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