package de.bitb.pantryplaner.ui.checklist

import android.os.Bundle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.core.misc.formatted
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.KEY_CHECKLIST_UUID
import de.bitb.pantryplaner.ui.base.composable.*
import de.bitb.pantryplaner.ui.base.naviChecklistToItems
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.comps.CategoryHeader
import de.bitb.pantryplaner.ui.comps.DeleteItemBackground
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog
import de.bitb.pantryplaner.ui.dialogs.EditItemDialog
import de.bitb.pantryplaner.ui.dialogs.FilterDialog

@AndroidEntryPoint
class ChecklistFragment : BaseFragment<ChecklistViewModel>() {
    companion object {
        const val APPBAR_TAG = "CheckAppbar"
        const val LAYOUT_BUTTON_TAG = "CheckLayoutButton"
        const val FILTER_BUTTON_TAG = "CheckFilterButton"
        const val ADD_BUTTON_TAG = "CheckAddButton"
        const val UNCHECK_BUTTON_TAG = "CheckUncheckButton"
        const val LIST_TAG = "CheckList"
        const val GRID_TAG = "CheckGrid"
    }

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
    override fun ScreenContent() {
        showGridLayout = remember { mutableStateOf(true) }
        showFilterDialog = remember { mutableStateOf(false) }
        showFinishDialog = remember { mutableStateOf(false) }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            content = { buildContent(it) },
            bottomBar = { buildBottomBar() },
        )

        if (showFilterDialog.value) {
            FilterDialog(
                viewModel.filterBy,
                onConfirm = { showFilterDialog.value = false },
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
            modifier = Modifier.testTag(APPBAR_TAG),
            title = {
                Text(
                    getString(
                        R.string.check_title,
                        checklist?.data?.name ?: "Loading..."
                    )
                )
            },
            actions = {
                IconButton(
                    onClick = { showGridLayout.value = !showGridLayout.value },
                    modifier = Modifier.testTag(LAYOUT_BUTTON_TAG)
                ) {
                    Icon(
                        imageVector = if (showGridLayout.value) Icons.Default.GridOff else Icons.Default.GridOn,
                        contentDescription = "Layout button"
                    )
                }
                IconButton(
                    onClick = { showFilterDialog.value = !showFilterDialog.value },
                    modifier = Modifier.testTag(FILTER_BUTTON_TAG)
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
                        .testTag(UNCHECK_BUTTON_TAG),
                    onClick = { showFinishDialog.value = true },
                    content = { Text("Erledigen") }
                )
            }
            Box(
                modifier = Modifier.padding(8.dp)
            ) {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .testTag(ADD_BUTTON_TAG),
                    onClick = { naviChecklistToItems(viewModel.checkListId) }
                ) { Icon(Icons.Filled.Add, contentDescription = "Add Item") }
            }
        }
    }

    @Composable
    private fun buildContent(innerPadding: PaddingValues) {
        val items by viewModel.itemMap.collectAsState(null)
        when {
            items is Resource.Error -> {
                showSnackBar("ERROR".asResString())
                ErrorScreen(items!!.message!!.asString())
            }
            items == null -> LoadingIndicator()
            items?.data?.isEmpty() == true -> EmptyListComp(getString(R.string.no_items))
            else -> CheckList(innerPadding, items!!.data!!)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun CheckList(
        innerPadding: PaddingValues,
        items: Map<String, List<Item>>
    ) {
        val showItems = remember { mutableStateMapOf<String, Boolean>() }
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
                    items.forEach { (header, list) ->
                        val headerText = header.ifBlank { "Keine" }
                        stickyGridHeader {
                            CategoryHeader(
                                headerText,
                                list.first().color,
                                showItems,
                                viewModel::editCategory,
                            )
                        }
                        if (showItems[headerText] != false) {
                            items(list.size) { CheckListItem(list[it]) }
                        }
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
                    items.forEach { (header, list) ->
                        val headerText = header.ifBlank { "Keine" }
                        stickyHeader {
                            CategoryHeader(
                                headerText,
                                list.first().color,
                                showItems,
                                viewModel::editCategory,
                            )
                        }
                        if (showItems[headerText] != false) {
                            items(list.size) { CheckListItem(list[it]) }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun CheckListItem(item: Item) {
        val checkResp by viewModel.checkList.collectAsState(null)
        when {
            checkResp is Resource.Error -> ErrorScreen(checkResp!!.message!!.asString())
            checkResp?.data == null -> LoadingIndicator()
            else -> {
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
                        "Möchten Sie folgendes Item entfernen?\n${item.name}",
                        onConfirm = {
                            viewModel.removeItem(item)
                            showRemoveDialog = false
                        },
                        onDismiss = { showRemoveDialog = false },
                    )
                }

                if (showEditDialog) {
                    EditItemDialog(
                        item = item,
                        onConfirm = { name, category ->
                            viewModel.editItem(item, name, category)
                            showEditDialog = false
                        },
                        onDismiss = { showEditDialog = false },
                    )
                }

                val checklist = checkResp!!.data!!
                val checkItem = checklist.items.first { it.uuid == item.uuid }
                val amountDisplay: Double = checkItem.amount
                val amountState =
                    remember { mutableStateOf(TextFieldValue(amountDisplay.formatted)) }
                val error = viewModel.itemErrorList.collectAsState(listOf())
                val color =
                    (if (error.value.contains(item.uuid)) BaseColors.FireRed
                    else BaseColors.White)
                SwipeToDismiss(
                    modifier = Modifier.padding(2.dp),
                    state = dismissState,
                    directions = setOf(DismissDirection.StartToEnd),
                    background = { DeleteItemBackground() },
                    dismissContent = {
                        Card(
                            elevation = 4.dp,
                            border = BorderStroke(2.dp, item.color),
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
                                    checkItem.checked,
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
                                            fontSize = 10.sp,
                                        )
                                    }
                                    Text(
                                        item.name,
                                        modifier = Modifier,
                                        textDecoration = if (checkItem.checked) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                }
                                val interactionSource = remember { MutableInteractionSource() }
                                BasicTextField(
                                    amountState.value,
                                    modifier = Modifier.padding(4.dp).width(60.dp)
                                        .background(color.copy(alpha = .5f)),
                                    textStyle = TextStyle.Default.copy(
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center,
                                    ),
                                    maxLines = 1,
                                    interactionSource = interactionSource,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    onValueChange = {
                                        if (it.text.length < 8) {
                                            amountState.value = it
                                            viewModel.changeItemAmount(item.uuid, it.text)
                                        }
                                    },
                                ) { innerTextField ->
                                    TextFieldDefaults.TextFieldDecorationBox(
                                        value = amountState.value.text,
                                        visualTransformation = VisualTransformation.None,
                                        innerTextField = innerTextField,
                                        singleLine = true,
                                        enabled = true,
                                        interactionSource = interactionSource,
                                        contentPadding = PaddingValues(0.dp),
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}