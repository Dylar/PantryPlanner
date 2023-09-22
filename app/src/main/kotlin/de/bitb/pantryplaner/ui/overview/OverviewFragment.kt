package de.bitb.pantryplaner.ui.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.comps.EmptyListComp
import de.bitb.pantryplaner.ui.base.comps.ErrorScreen
import de.bitb.pantryplaner.ui.base.comps.GridListLayout
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator
import de.bitb.pantryplaner.ui.base.comps.dissmissItem
import de.bitb.pantryplaner.ui.base.naviOverviewToItems
import de.bitb.pantryplaner.ui.base.naviToChecklist
import de.bitb.pantryplaner.ui.base.naviToProfile
import de.bitb.pantryplaner.ui.base.naviToRefresh
import de.bitb.pantryplaner.ui.base.testTags.OverviewPageTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import de.bitb.pantryplaner.ui.dialogs.AddChecklistDialog
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog

@Preview(showBackground = true)
@Composable
private fun PreferenceCategoryPreview() {
    OverviewFragment()
}

@AndroidEntryPoint
class OverviewFragment : BaseFragment<OverviewViewModel>() {
    override val viewModel: OverviewViewModel by viewModels()

    private lateinit var showGridLayout: MutableState<Boolean>
    private lateinit var showAddDialog: MutableState<Boolean>

    @Composable
    override fun screenContent() {
        showGridLayout = remember { mutableStateOf(true) }
        showAddDialog = remember { mutableStateOf(false) }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            content = { buildContent(it) },
            floatingActionButton = { buildFab() },
        )

        if (showAddDialog.value) {
            val users = viewModel.getConnectedUsers().observeAsState()
            if (users.value is Resource.Success) {
                AddChecklistDialog(
                    users.value!!.data!!,
                    onConfirm = { name, sharedWith ->
                        viewModel.addChecklist(name, sharedWith)
                        showAddDialog.value = false
                    },
                    onDismiss = { showAddDialog.value = false },
                )
            }
        }
    }

    @Composable
    private fun buildAppBar() {
        TopAppBar(
            modifier = Modifier.testTag(OverviewPageTag.AppBar),
            title = { Text(getString(R.string.overview_title)) },
            actions = {
                IconButton(
                    onClick = ::naviToProfile,
                    modifier = Modifier.testTag(OverviewPageTag.ProfileButton)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "profile button"
                    )
                }
                IconButton(
                    onClick = { showGridLayout.value = !showGridLayout.value },
                    modifier = Modifier.testTag(OverviewPageTag.LayoutButton)
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
                modifier = Modifier.testTag(OverviewPageTag.AddButton),
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
                modifier = Modifier.testTag(OverviewPageTag.ToItemsButton),
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
        val checklists by viewModel.checkList.observeAsState(null)
        when {
            checklists is Resource.Error -> ErrorScreen(checklists!!.message!!.asString())
            checklists == null -> LoadingIndicator()
            checklists?.data?.isEmpty() == true -> EmptyListComp(getString(R.string.no_checklists))
            else -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TODO depending if needed
                Button(
                    onClick = ::naviToRefresh,
                    shape = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.elevation(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCartCheckout,
                            contentDescription = "Refresh button"
                        )
                        Text(text = "Bestand aktualisieren")
                    }
                }
                GridListLayout(
                    innerPadding,
                    showGridLayout,
                    checklists!!.data!!.mapKeys { if (it.key) "Erledigt" else "Checklist" },
                    { it.color },
                ) { _, item -> checkListItem(item) }
            }
        }
    }

    @Composable
    private fun checkListItem(checklist: Checklist) {
        val showUnfinishDialog = remember { mutableStateOf(false) }
        if (showUnfinishDialog.value) {
            ConfirmDialog(
                "Checklist öffnen?",
                "Checkliste ist schon erledigt, möchtest Sie sie wieder öffnen und die Items aus dem Bestand entfernen?",
                onConfirm = {
                    showUnfinishDialog.value = false
                    viewModel.unfinishChecklist(checklist)
                },
                onDismiss = { showUnfinishDialog.value = false },
            )
        }

        dissmissItem(
            checklist.name,
            checklist.color,
            onSwipe = { viewModel.removeChecklist(checklist) },
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