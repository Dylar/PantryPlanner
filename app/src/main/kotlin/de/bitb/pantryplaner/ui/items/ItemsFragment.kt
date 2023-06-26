package de.bitb.pantryplaner.ui.items

import android.os.Bundle
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
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.dialogs.*

@AndroidEntryPoint
class ItemsFragment : BaseFragment<ItemsViewModel>() {
    companion object {
        const val APPBAR_TAG = "ItemAppbar"
        const val LAYOUT_BUTTON_TAG = "ItemLayoutButton"
        const val FILTER_BUTTON_TAG = "ItemFilterButton"
        const val ADD_BUTTON_TAG = "ItemAddButton"
        const val ADD_TO_BUTTON_TAG = "ItemAddToButton"
        const val LIST_TAG = "CheckList"
        const val GRID_TAG = "CheckGrid"
    }

    override val viewModel: ItemsViewModel by viewModels()

    private lateinit var showGridLayout: MutableState<Boolean>
    private lateinit var showFilterDialog: MutableState<Boolean>
    private lateinit var showAddDialog: MutableState<Boolean>
    private lateinit var showAddToDialog: MutableState<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initItems(arguments?.getString(KEY_CHECKLIST_UUID))
    }

    @Composable
    override fun ScreenContent() {
        showGridLayout = remember { mutableStateOf(true) }
        showFilterDialog = remember { mutableStateOf(false) }
        showAddDialog = remember { mutableStateOf(false) }
        showAddToDialog = remember { mutableStateOf(false) }

        onBack { onDismiss ->
            ConfirmDialog(
                "Discard changes?",
                "Möchten Sie die Item Auswahl verwerfen?",
                onConfirm = { navController.popBackStack() },
                onDismiss = { onDismiss() },
            )
        }
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

        if (showAddDialog.value) {
            AddItemDialog(
                onConfirm = { name, category, color, close ->
                    viewModel.addItem(name, category, color)
                    if (close) {
                        showAddDialog.value = false
                    }
                },
                onDismiss = { showAddDialog.value = false },
            )
        }

        if (showAddToDialog.value) {
            ConfirmDialog(
                // TODO select checklist dialog
                "Hinzufügen?",
                "Möchten Sie alle markierten Items der Checklist hinzufügen?",
                onConfirm = {
                    viewModel.addToChecklist(viewModel.fromChecklist!!)
                    showAddToDialog.value = false
                },
                onDismiss = { showAddToDialog.value = false },
            )
        }
    }

    @Composable
    private fun buildAppBar() {
        TopAppBar(
            modifier = Modifier.testTag(APPBAR_TAG),
            title = { Text(getString(R.string.items_title)) },
            actions = {
                IconButton(
                    modifier = Modifier.testTag(LAYOUT_BUTTON_TAG),
                    onClick = { showGridLayout.value = !showGridLayout.value },
                ) {
                    Icon(
                        imageVector = if (showGridLayout.value) Icons.Default.GridOff else Icons.Default.GridOn,
                        contentDescription = "Layout button"
                    )
                }
                IconButton(
                    modifier = Modifier.testTag(FILTER_BUTTON_TAG),
                    onClick = { showFilterDialog.value = !showFilterDialog.value },
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
            if (viewModel.fromChecklist != null) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(2f)
                        .background(Color.Transparent)
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag(ADD_TO_BUTTON_TAG),
                        onClick = { showAddToDialog.value = true },
                        content = { Text("Checkliste hinzufügen") }
                    )
                }
            }
            Box(
                modifier = Modifier.padding(8.dp)
                    .background(Color.Transparent)
            ) {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .testTag(ADD_BUTTON_TAG),
                    onClick = { showAddDialog.value = true }
                ) { Icon(Icons.Filled.Add, contentDescription = "Add Item") }
            }
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
            else -> ItemList(innerPadding, items!!.data!!)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ItemList(innerPadding: PaddingValues, items: Map<String, List<Item>>) {
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
                    items.forEach { (headerText, list) ->
                        if (headerText.isNotBlank()) {
                            stickyGridHeader { Header(headerText) }
                        }
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
                    items.forEach { (headerText, list) ->
                        if (headerText.isNotBlank()) {
                            stickyHeader { Header(headerText) }
                        }
                        items(list.size) { CheckListItem(list[it]) }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun Header(category: String) {
        var showEditDialog by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.combinedClickable(
                    onClick = {}, // required? Oo
                    onLongClick = { showEditDialog = true }
                )
            ) {
                Text(
                    category,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        if (showEditDialog) {
            EditCategoryDialog(
                category,
                onConfirm = {
                    viewModel.editCategory(category, it)
                    showEditDialog = false
                },
                onDismiss = { showEditDialog = false },
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun CheckListItem(item: Item) {
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
                onConfirm = { name, category, color ->
                    viewModel.editItem(item, name, category, color)
                    showEditDialog = false
                },
                onDismiss = { showEditDialog = false },
            )
        }

        SwipeToDismiss(
            modifier = Modifier
                .padding(2.dp),
            state = dismissState,
            directions = setOf(DismissDirection.StartToEnd),
            background = { DeleteItemBackground() },
            dismissContent = {
                Card(
                    elevation = 4.dp,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .combinedClickable(
                            onClick = { viewModel.checkItem(item.uuid) },
                            onLongClick = { showEditDialog = true },
                        ),
                ) {
                    val checkedItems = viewModel.checkedItems.collectAsState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (viewModel.fromChecklist != null) {
                            Checkbox(
                                checkedItems.value.contains(item.uuid),
                                modifier = Modifier
                                    .weight(.2f),
                                onCheckedChange = { viewModel.checkItem(item.uuid) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = item.color,
                                    uncheckedColor = item.color
                                )
                            )
                        }
                        Column(
                            modifier = Modifier
                                .padding(start = 2.dp)
                                .weight(.7f),
                        )
                        {
                            if (item.category.isNotBlank()) {
                                Text(
                                    item.category,
                                    modifier = Modifier,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Start
                                )
                            }
                            Text(
                                item.name,
                                modifier = Modifier,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Start
                            )
                        }
                        if (viewModel.fromChecklist == null) {
                            val error = viewModel.itemErrorList.collectAsState(listOf())
                            val color =
                                (if (error.value.contains(item.uuid)) BaseColors.FireRed
                                else BaseColors.White)
                            val amountState =
                                remember { mutableStateOf(TextFieldValue(item.amount.formatted)) }
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

                        if (viewModel.fromChecklist != null) {
                            Text(
                                item.amount.formatted,
                                modifier = Modifier.padding(end = 4.dp),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            }
        )
    }
}