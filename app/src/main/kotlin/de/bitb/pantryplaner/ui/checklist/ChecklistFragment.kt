package de.bitb.pantryplaner.ui.checklist

import android.os.Bundle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.*
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
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.KEY_CHECKLIST_UUID
import de.bitb.pantryplaner.ui.base.TestTags
import de.bitb.pantryplaner.ui.base.comps.*
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

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            content = { buildContent(it) },
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
    private fun buildAppBar() {
        val checklist by viewModel.checkList.collectAsState(null)
        TopAppBar(
            modifier = Modifier.testTag(TestTags.ChecklistPage.AppBar.name),
            title = {
                Text(
                    checklist?.data?.name ?: getString(R.string.loading_text),
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
    private fun buildContent(innerPadding: PaddingValues) {
        val items by viewModel.itemMap.collectAsState(null)
        val categorys = items?.data?.keys?.toList() ?: listOf()
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
                viewModel::editCategory
            ) { _, item -> checkListItem(item, categorys) }
        }
    }

    @Composable
    private fun checkListItem(item: Item, categorys: List<String>) {
        val checkResp by viewModel.checkList.collectAsState(null)
        when {
            checkResp is Resource.Error -> ErrorScreen(checkResp!!.message!!.asString())
            checkResp?.data == null -> Card(
                elevation = 4.dp,
                border = BorderStroke(2.dp, item.color),
                modifier = Modifier.padding(vertical = 4.dp),
            ) { LoadingIndicator() }
            else -> {
                val checklist = checkResp!!.data!!
                val checkItem = checklist.items.firstOrNull { it.uuid == item.uuid } ?: return

                val showEditDialog = remember { mutableStateOf(false) }
                useEditItemDialog(showEditDialog, item, categorys, viewModel::editItem)

                dissmissItem(
                    item.name,
                    item.color,
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
    }
}