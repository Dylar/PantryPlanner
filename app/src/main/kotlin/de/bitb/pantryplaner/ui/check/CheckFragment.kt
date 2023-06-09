package de.bitb.pantryplaner.ui.check

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
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
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.composable.ErrorScreen
import de.bitb.pantryplaner.ui.base.composable.LoadingIndicator
import de.bitb.pantryplaner.ui.base.composable.asResString
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.info.InfoDialog

@AndroidEntryPoint
class CheckFragment : BaseFragment<CheckViewModel>() {
    companion object {
        const val APPBAR_TAG = "CheckAppbar"
        const val INFO_BUTTON_TAG = "CheckInfoButton"
        const val ADD_BUTTON_TAG = "CheckAddButton"
        const val UNCHECK_BUTTON_TAG = "CheckUncheckButton"
        const val LIST_TAG = "CheckList"
    }

    override val viewModel: CheckViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
        var showInfoDialog by remember { mutableStateOf(false) }
        var showAddDialog by remember { mutableStateOf(false) }
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text(getString(R.string.check_title)) },
                    actions = {
                        IconButton(
                            onClick = { showInfoDialog = true },
                            modifier = Modifier.testTag(INFO_BUTTON_TAG)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info dialog"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.testTag(ADD_BUTTON_TAG),
                    onClick = { showAddDialog = true }
                ) { Icon(Icons.Filled.Add, contentDescription = "Add Item") }
            },
        ) { innerPadding ->
            val list by viewModel.checkList.collectAsState(null)
            if (list is Resource.Error) {
                showSnackBar("ERROR".asResString())
                ErrorScreen(list!!.message!!.asString())
            } else {
                CheckList(innerPadding, list?.data)
            }
        }

        if (showInfoDialog) {
            InfoDialog { showInfoDialog = false }
        }

        if (showAddDialog) {
            AddDialog(
                onConfirm = { name, close ->
                    viewModel.addItem(name)
                    if (close) {
                        showAddDialog = false
                    }
                },
                onDismiss = { showAddDialog = false },
            )
        }
    }

    @Composable
    fun CheckList(innerPadding: PaddingValues, check: List<Item>?) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(innerPadding)
        ) {
            when {
                check == null -> LoadingIndicator()
                check.isEmpty() -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text(
                        modifier = Modifier.fillMaxSize(),
                        text = getString(R.string.no_check),
                        textAlign = TextAlign.Center,
                    )
                }
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        FloatingActionButton(
                            modifier = Modifier.testTag(UNCHECK_BUTTON_TAG).padding(bottom = 8.dp),
                            onClick = { viewModel.uncheckAllItems() }
                        ) { Icon(Icons.Filled.Close, contentDescription = "Uncheck Items") }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag(LIST_TAG),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            contentPadding = innerPadding
                        ) { items(check.size) { CheckListItem(check[it]) } }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun CheckListItem(item: Item) {
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
            RemoveDialog(
                item,
                onConfirm = {
                    viewModel.removeItem(it)
                    showRemoveDialog = false
                },
                onDismiss = { showRemoveDialog = false },
            )
        }

        SwipeToDismiss(
            state = dismissState,
            directions = setOf(DismissDirection.StartToEnd),
            background = {
                Card(
                    elevation = 4.dp,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.checkItem(item) }
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BaseColors.FireRed)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Delete",
                            fontSize = 20.sp,
                            color = BaseColors.White
                        )
                    }
                }
            },
            dismissContent = {
                Card(
                    elevation = 4.dp,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.checkItem(item) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            item.name,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        )
                        Checkbox(
                            item.checked,
                            onCheckedChange = { viewModel.checkItem(item) },
                        )
                    }
                }
            }
        )
    }
}