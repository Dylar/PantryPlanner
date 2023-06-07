package de.bitb.pantryplaner.ui.check

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.R
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.composable.LoadingIndicator
import de.bitb.pantryplaner.ui.info.InfoDialog

@AndroidEntryPoint
class CheckFragment : BaseFragment<CheckViewModel>() {
    companion object {
        const val APPBAR_TAG = "CheckAppbar"
        const val INFO_BUTTON_TAG = "CheckInfobutton"
        const val ADD_BUTTON_TAG = "CheckAddbutton"
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
                    navigationIcon = {
                        IconButton(
                            onClick = { showInfoDialog = true },
                            modifier = Modifier.testTag(INFO_BUTTON_TAG)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Toggle drawer"
                            )
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.testTag(ADD_BUTTON_TAG),
                    onClick = { showAddDialog = true }
                ) { Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan Buddy") }
            },
        ) { innerPadding ->
            val list by viewModel.checkList.observeAsState(null)
            CheckList(innerPadding, list)
        }

        if (showInfoDialog) {
            InfoDialog { showInfoDialog = false }
        }

        if (showAddDialog) {
            AddDialog(
                onConfirm = viewModel::addItem,
                onDismiss =  { showAddDialog = false },
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
                check.isEmpty() -> Text(
                    modifier = Modifier.fillMaxSize(),
                    text = getString(R.string.no_check)
                )
                else -> {
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

    @Composable
    fun CheckListItem(item: Item) {
        Card(
            elevation = 4.dp,
            modifier = Modifier
                .padding(8.dp)
                .clickable { viewModel.checkItem(item) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
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
}