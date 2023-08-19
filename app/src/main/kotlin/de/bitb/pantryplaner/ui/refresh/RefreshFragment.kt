package de.bitb.pantryplaner.ui.refresh

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import de.bitb.pantryplaner.core.misc.formatted
import de.bitb.pantryplaner.core.misc.parseDateString
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.TestTags
import de.bitb.pantryplaner.ui.base.comps.*
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.comps.SelectItemHeader
import de.bitb.pantryplaner.ui.dialogs.AddChecklistDialog

@AndroidEntryPoint
class RefreshFragment : BaseFragment<RefreshViewModel>() {
    override val viewModel: RefreshViewModel by viewModels()

    private lateinit var showGridLayout: MutableState<Boolean>

    @Composable
    override fun screenContent() {
        // TODO show tooltip and explain
        showGridLayout = remember { mutableStateOf(true) }

        val items by viewModel.itemList.collectAsState(null)
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            content = { buildContent(it, items) },
            floatingActionButton = { buildFab(items) }
        )
    }

    @Composable
    private fun buildAppBar() {
        TopAppBar(
            modifier = Modifier.testTag(TestTags.RefreshPage.AppBar.name),
            title = { Text(getString(R.string.refresh_title)) },
            actions = {
                IconButton(
                    modifier = Modifier.testTag(TestTags.RefreshPage.LayoutButton.name),
                    onClick = { showGridLayout.value = !showGridLayout.value },
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
    private fun buildFab(items: Resource<Map<String, List<Item>>>?) {
        if (items != null && items is Resource.Success) {
            val hasReminderItems = items.data?.any { entry ->
                entry.value.any { it.remindIt(parseDateString(entry.key)) }
            }
            if (hasReminderItems == true) {
                val showAddToDialog = remember { mutableStateOf(false) }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center,
                ) {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Neue Liste anlegen") },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Add to list FAB",
                            )
                        },
                        onClick = {
                            val checkedItems = viewModel.checkedItems.value
                            if (checkedItems.isNotEmpty()) showAddToDialog.value = true
                            else showSnackBar("Wähle mindestens 1 Item".asResString())
                        },
                    )
                }

                if (showAddToDialog.value) {
                    val users = viewModel.getConnectedUsers().observeAsState()
                    if (users.value is Resource.Success) {
                        AddChecklistDialog(
                            users.value!!.data!!,
                            onConfirm = { name, sharedWith ->
                                viewModel.addToNewChecklist(name, sharedWith)
                                showAddToDialog.value = false
                            },
                            onDismiss = { showAddToDialog.value = false },
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun buildContent(
        innerPadding: PaddingValues,
        items: Resource<Map<String, List<Item>>>?,
    ) {
        when {
            items is Resource.Error -> {
                showSnackBar("ERROR".asResString())
                ErrorScreen(items.message!!.asString())
            }
            items == null -> LoadingIndicator()
            items.data?.isEmpty() == true -> EmptyListComp(getString(R.string.no_items))
            else -> GridListLayout(
                innerPadding,
                showGridLayout,
                items.data!!,
                { it.color },
            ) { header, item -> listItem(header, item) }
        }
    }

    @Composable
    private fun listItem(header: String, item: Item) {
        if (item.isFresh(parseDateString(header))) {
            RefreshItem(item)
        } else {
            RemindItem(item)
        }
    }

    @Composable
    private fun RefreshItem(item: Item) {
        clearItem(
            item.name,
            item.color,
            onSwipe = { viewModel.clearItemAmount(item.uuid) },
            onClick = { viewModel.checkItem(item.uuid) },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    item.name,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = item.amount.formatted,
                    modifier = Modifier
                        .width(50.dp)
                        .padding(start = 4.dp)
                        .background(BaseColors.LightGray.copy(alpha = .1f)),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    private fun RemindItem(item: Item) {
        val checkedItems = viewModel.checkedItems.collectAsState()
        Box(modifier = Modifier.padding(2.dp)) {
            Card(
                elevation = 4.dp,
                border = BorderStroke(2.dp, item.color),
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(onClick = { viewModel.checkItem(item.uuid) }),
            ) {
                SelectItemHeader(
                    item,
                    checkedItems.value.contains(item.uuid),
                    checkItem = viewModel::checkItem
                )
            }
        }
    }
}