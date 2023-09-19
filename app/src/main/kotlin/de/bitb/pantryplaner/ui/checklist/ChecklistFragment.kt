package de.bitb.pantryplaner.ui.checklist

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Checklist
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.StockItem
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.KEY_CHECKLIST_UUID
import de.bitb.pantryplaner.ui.base.TestTags
import de.bitb.pantryplaner.ui.base.comps.EmptyListComp
import de.bitb.pantryplaner.ui.base.comps.ErrorScreen
import de.bitb.pantryplaner.ui.base.comps.GridListLayout
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator
import de.bitb.pantryplaner.ui.base.comps.buildUserDropDown
import de.bitb.pantryplaner.ui.base.comps.dissmissItem
import de.bitb.pantryplaner.ui.base.naviChecklistToItems
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.comps.AddSubRow
import de.bitb.pantryplaner.ui.comps.SelectItemHeader
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog
import de.bitb.pantryplaner.ui.dialogs.FilterDialog
import de.bitb.pantryplaner.ui.dialogs.useEditItemDialog

@AndroidEntryPoint
class ChecklistFragment : BaseFragment<ChecklistViewModel>() {
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
    override fun screenContent() {
        showGridLayout = remember { mutableStateOf(true) }
        showFilterDialog = remember { mutableStateOf(false) }
        showFinishDialog = remember { mutableStateOf(false) }

        val checkModel by viewModel.checkModel.observeAsState(null)
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar(checkModel) },
            content = { buildContent(it, checkModel) },
            floatingActionButton = { buildFab() },
        )

        if (showFilterDialog.value) {
            FilterDialog(
                viewModel.filterBy.value,
                onConfirm = {
                    viewModel.filterBy.value = it
                    showFilterDialog.value = false
                },
                onDismiss = { showFilterDialog.value = false },
            )
        }

        if (showFinishDialog.value) {
            ConfirmDialog(
                "Fertig?",
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
    private fun buildAppBar(checkModel: Resource<CheckModel>?) {
        TopAppBar(
            modifier = Modifier.testTag(TestTags.ChecklistPage.AppBar.name),
            title = {
                Text(
                    checkModel?.data?.checklist?.name ?: getString(R.string.loading_text),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            actions = {
                IconButton(
                    onClick = { showGridLayout.value = !showGridLayout.value },
                    modifier = Modifier.testTag(TestTags.ChecklistPage.LayoutButton.name)
                ) {
                    Icon(
                        imageVector = if (showGridLayout.value) Icons.Default.GridOff else Icons.Default.GridOn,
                        contentDescription = "Layout button"
                    )
                }
                IconButton(
                    onClick = { showFilterDialog.value = !showFilterDialog.value },
                    modifier = Modifier.testTag(TestTags.ChecklistPage.FilterButton.name)
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
            SmallFloatingActionButton(
                onClick = { naviChecklistToItems(viewModel.checkListId) },
                containerColor = colors.secondaryVariant,
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "add FAB",
                    tint = Color.Black,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            ExtendedFloatingActionButton(
                text = { Text(text = "Erledigen") },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Finish FAB",
                    )
                },
                onClick = { showFinishDialog.value = true },
            )
        }
    }

    @Composable
    private fun buildContent(innerPadding: PaddingValues, checkModel: Resource<CheckModel>?) {
        when {
            checkModel?.data?.isLoading != false -> LoadingIndicator()
            checkModel is Resource.Error -> ErrorScreen(checkModel.message!!.asString())
            else -> Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
            ) {
                val model = checkModel.data
                val items = model.items!!
                val categorys = items.keys.toList()
                val allUsers = model.allUser!!
                val selectedUser = remember { mutableStateOf(model.sharedUser!!) }
                buildUserDropDown(
                    "Checkliste wird nicht geteilt",
                    allUsers,
                    selectedUser,
                    canChange = model.isCreator == true
                ) {
                    viewModel.setSharedWith(it)
                }
                if (items.isEmpty()) {
                    EmptyListComp(getString(R.string.no_items))
                } else {
                    val stockItems = model.stockItems!!
                    GridListLayout(
                        innerPadding,
                        showGridLayout,
                        items,
                        { stockItems.values.first().color }, // TODO color?
                        viewModel::editCategory
                    ) { _, item ->
                        checkListItem(
                            model.checklist!!,
                            categorys,
                            allUsers,
                            item,
                            stockItems[item.uuid]!!,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun checkListItem(
        checklist: Checklist,
        categorys: List<String>,
        users: List<User>,
        item: Item,
        stockItem: StockItem,
    ) {
        val checkItem = checklist.items.firstOrNull { it.uuid == item.uuid } ?: return

        val showEditDialog = remember { mutableStateOf(false) }
        useEditItemDialog(
            showEditDialog,
            stockItem,
            item,
            categorys,
            users,
        ) { si, i, _ -> viewModel.editItem(si, i) }

        dissmissItem(
            item.name,
            stockItem.color,
            onSwipe = { viewModel.removeItem(item) },
            onClick = { viewModel.checkItem(item.uuid) },
            onLongClick = { showEditDialog.value = true },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                SelectItemHeader(
                    stockItem,
                    item,
                    checkItem.checked,
                    true,
                    viewModel::checkItem
                )
                val errors = viewModel.itemErrorList.collectAsState(listOf())
                val color =
                    if (errors.value.contains(item.uuid)) BaseColors.FireRed
                    else BaseColors.White
                AddSubRow(
                    checkItem.amount,
                    color
                ) { viewModel.changeItemAmount(item.uuid, it) }
            }
        }
    }
}