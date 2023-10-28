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
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.comps.EmptyListComp
import de.bitb.pantryplaner.ui.base.comps.ErrorScreen
import de.bitb.pantryplaner.ui.base.comps.GridListLayout
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator
import de.bitb.pantryplaner.ui.base.comps.dissmissItem
import de.bitb.pantryplaner.ui.base.naviOverviewToItems
import de.bitb.pantryplaner.ui.base.naviToChecklist
import de.bitb.pantryplaner.ui.base.naviToProfile
import de.bitb.pantryplaner.ui.base.testTags.ChecklistTag
import de.bitb.pantryplaner.ui.base.testTags.OverviewPageTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog
import de.bitb.pantryplaner.ui.dialogs.useAddChecklistDialog
import de.bitb.pantryplaner.ui.dialogs.useEditChecklistDialog

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

        val modelResp by viewModel.overviewModel.observeAsState(null)
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            content = { buildContent(it, modelResp) },
            floatingActionButton = { buildFab(modelResp) },
        )
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
    private fun buildFab(modelResp: Resource<OverviewModel>?) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
        ) {
            if (modelResp !is Resource.Error &&
                modelResp?.data?.isLoading == false &&
                modelResp.data.stocks?.isNotEmpty() != false
            ) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.testTag(OverviewPageTag.NewChecklistButton),
                    text = { Text(text = "Checklist") },
                    onClick = { showAddDialog.value = true },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "add checklist FAB",
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

// TODO open multi adding -> add template or checklist -> no everything is a checklist ... or more FABs

            ExtendedFloatingActionButton(
                modifier = Modifier.testTag(OverviewPageTag.StockButton),
                text = { Text(text = "Bestand") },
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
    private fun buildContent(innerPadding: PaddingValues, modelResp: Resource<OverviewModel>?) {
        when {
            modelResp is Resource.Error -> ErrorScreen(modelResp.message!!.asString())
            modelResp?.data?.isLoading != false -> LoadingIndicator()
            else -> {
                val model = modelResp.data
                val users = model.connectedUser!!
                val stocks = model.stocks!!
                val checkLists = model.checkList!!
                useAddChecklistDialog(
                    showDialog = showAddDialog,
                    users = users,
                    stocks = stocks,
                    onEdit = { checklist, _ ->
                        viewModel.addChecklist(checklist)
                        showAddDialog.value = false
                    },
                )
                if (checkLists.isEmpty()) {
                    EmptyListComp(getString(R.string.no_checklists))
                    return
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // TODO depending if needed + fix this page
//                    Button(
//                        onClick = ::naviToRefresh,
//                        shape = MaterialTheme.shapes.medium,
//                        elevation = ButtonDefaults.elevation(8.dp),
//                        modifier = Modifier.padding(8.dp)
//                    ) {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.ShoppingCartCheckout,
//                                contentDescription = "Refresh button"
//                            )
//                            Text(text = "Bestand aktualisieren")
//                        }
//                    }
                    GridListLayout(
                        innerPadding,
                        showGridLayout,
                        checkLists.mapKeys { if (it.key) "Erledigt" else "Checklist" },
                        { it.color },
                    ) { _, item -> checkListItem(stocks, item, users) }
                }
            }
        }
    }

    @Composable
    private fun checkListItem(stocks: List<Stock>, checklist: Checklist, users: List<User>) {
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

        val showEditDialog = remember { mutableStateOf(false) }
        useEditChecklistDialog(
            showDialog = showEditDialog,
            checklist = checklist,
            users = users,
            stocks = stocks,
            onEdit = { check, _ ->
                showEditDialog.value = false
                viewModel.editChecklist(check)
            }
        )

        dissmissItem(
            checklist.name,
            checklist.color,
            onSwipe = { viewModel.removeChecklist(checklist) },
            onClick = {
                if (checklist.finished) showUnfinishDialog.value = true
                else naviToChecklist(checklist.uuid)
            },
            onLongClick = { showEditDialog.value = true }
        ) {
            Row(
                modifier = Modifier
                    .testTag(ChecklistTag(checklist.name))
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