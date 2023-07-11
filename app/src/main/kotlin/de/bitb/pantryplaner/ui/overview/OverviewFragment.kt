package de.bitb.pantryplaner.ui.overview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.comps.*
import de.bitb.pantryplaner.ui.base.naviOverviewToItems
import de.bitb.pantryplaner.ui.base.naviToChecklist
import de.bitb.pantryplaner.ui.base.naviToReleaseNotes
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
        const val TO_ITEMS_BUTTON_TAG = "OverviewToItemsButton"
    }

    override val viewModel: OverviewViewModel by viewModels()

    private lateinit var showGridLayout: MutableState<Boolean>
    private lateinit var showAddDialog: MutableState<Boolean>
    private lateinit var showUnfinishDialog: MutableState<Boolean>

    @Composable
    override fun ScreenContent() {
        showGridLayout = remember { mutableStateOf(true) }
        showAddDialog = remember { mutableStateOf(false) }
        showUnfinishDialog = remember { mutableStateOf(false) }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            content = { buildContent(it) },
            floatingActionButton = { buildFab() },
        )

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
        val showInfoDialog = remember { mutableStateOf(false) }
        if (showInfoDialog.value) {
            InfoDialog(naviToReleaseNotes = ::naviToReleaseNotes) { showInfoDialog.value = false }
        }

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
    private fun buildFab() {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
        ) {
            SmallFloatingActionButton(
                modifier = Modifier.testTag(ADD_BUTTON_TAG),
                onClick = { showAddDialog.value = true },
                containerColor = MaterialTheme.colors.secondaryVariant,
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "add checklist FAB",
                    tint = Color.Black,
                )
            }

            //TODO add testTags
// TODO open multi adding -> add template or checklist -> no everything is a checklist ... or more FABs
            Spacer(modifier = Modifier.height(8.dp))

            ExtendedFloatingActionButton(
                modifier = Modifier.testTag(TO_ITEMS_BUTTON_TAG),
                text = { Text(text = "Zum Bestand") },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.FormatListBulleted,
                        contentDescription = "To stock FAB",
                    )
                },
                onClick = ::naviOverviewToItems,
            )
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
            else -> GridListLayout(
                innerPadding,
                showGridLayout,
                checklists!!.data!!.mapKeys { if (it.key) "Erledigt" else "Checklist" },
                { it.color },
            ) { CheckListItem(it) }
        }
    }

    @Composable
    fun CheckListItem(checklist: Checklist) {
        if (showUnfinishDialog.value) {
            ConfirmDialog(
                "Checklist öffnen?",
                "Checkliste ist schon erledigt, möchtest Sie sie wieder öffnen und die Items aus dem Bestand entfernen?",
                onConfirm = { showUnfinishDialog.value = false },
                onDismiss = { showUnfinishDialog.value = false },
            )
        }

        val showRemoveDialog = remember { mutableStateOf(false) }
        DissmissItem(
            checklist.name,
            checklist.color,
            showRemoveDialog,
            onRemove = { viewModel.unfinishChecklist(checklist) },
            onClick = {
                if (checklist.finished) showUnfinishDialog.value = true
                else naviToChecklist(checklist.uuid)
            },
        ) {
            Row(
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            )
            {
                Text(
                    checklist.name,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    textDecoration = if (checklist.finished) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    checklist.progress,
                    modifier = Modifier,
                    fontSize = 16.sp,
                )
            }
        }
    }
}