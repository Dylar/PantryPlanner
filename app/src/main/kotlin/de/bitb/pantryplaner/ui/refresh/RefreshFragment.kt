package de.bitb.pantryplaner.ui.refresh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
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
    private lateinit var showAddToDialog: MutableState<Boolean>

    @Composable
    override fun screenContent() {
        showGridLayout = remember { mutableStateOf(true) }
        showAddToDialog = remember { mutableStateOf(false) }
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            content = { buildContent(it) },
            floatingActionButton = { buildFab() }
        )

        if (showAddToDialog.value) {
            AddChecklistDialog(
                onConfirm = { name ->
                    viewModel.addToNewChecklist(name)
                    showAddToDialog.value = false
                },
                onDismiss = { showAddToDialog.value = false },
            )
        }
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
    private fun buildFab() {
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
                onClick = { showAddToDialog.value = true },
            )
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
            ) { header, item -> listItem(header, item) }
        }
    }

    @Composable
    private fun listItem(header: String, item: Item) {
        if (item.remindIt(parseDateString(header))) {
            val checkedItems = viewModel.checkedItems.collectAsState()
            SelectItemHeader(
                item,
                checkedItems.value.contains(item.uuid),
                checkItem = viewModel::checkItem
            )
        } else {
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
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        item.name,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        item.amount.formatted,
                        modifier = Modifier
                            .width(50.dp)
                            .background(BaseColors.LightGray.copy(alpha = .1f)),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

            }
        }
    }
}