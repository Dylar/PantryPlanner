package de.bitb.pantryplaner.ui.stock

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.StockItem
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.comps.EmptyListComp
import de.bitb.pantryplaner.ui.base.comps.ErrorScreen
import de.bitb.pantryplaner.ui.base.comps.GridListLayout
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator
import de.bitb.pantryplaner.ui.base.comps.SearchBar
import de.bitb.pantryplaner.ui.base.comps.buildUserDropDown
import de.bitb.pantryplaner.ui.base.comps.dissmissItem
import de.bitb.pantryplaner.ui.base.comps.onBack
import de.bitb.pantryplaner.ui.base.highlightedText
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.testTags.ItemTag
import de.bitb.pantryplaner.ui.base.testTags.StockPageTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import de.bitb.pantryplaner.ui.comps.AddSubRow
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog
import de.bitb.pantryplaner.ui.dialogs.FilterDialog
import de.bitb.pantryplaner.ui.dialogs.useAddItemDialog
import de.bitb.pantryplaner.ui.dialogs.useEditItemDialog
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StockFragment : BaseFragment<StockViewModel>() {
    override val viewModel: StockViewModel by viewModels()

    private lateinit var showGridLayout: MutableState<Boolean>
    private lateinit var showFilterDialog: MutableState<Boolean>
    private lateinit var showAddDialog: MutableState<Boolean>
    private lateinit var showSearchBar: MutableState<Boolean>

    private val searchButtonIcon: ImageVector
        get() =
            if (showSearchBar.value) Icons.Default.Cancel
            else if (viewModel.filterBy.value.filterByTerm) Icons.Default.SavedSearch
            else Icons.Default.Search

    @Composable
    override fun screenContent() {
        showGridLayout = remember { mutableStateOf(true) }
        showFilterDialog = remember { mutableStateOf(false) }
        showAddDialog = remember { mutableStateOf(false) }
        showSearchBar = remember { mutableStateOf(false) }

        val filter by viewModel.filterBy.collectAsState(Filter())
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
            topBar = { buildAppBar(filter) },
            content = { buildContent(it) },
            floatingActionButton = { buildFab() }
        )

        if (showFilterDialog.value) {
            FilterDialog(
                filter,
                onConfirm = {
                    viewModel.filterBy.value = it
                    showFilterDialog.value = false
                },
                onDismiss = { showFilterDialog.value = false },
            )
        }
    }

    @Composable
    private fun buildAppBar(filter: Filter) {
        TopAppBar(
            modifier = Modifier.testTag(StockPageTag.AppBar),
            title = {
                val isSearching by viewModel.isSearching.collectAsState(false)
                if (showSearchBar.value) SearchBar(
                    showSearchBar,
                    isSearching,
                    filter.searchTerm,
                    viewModel::search,
                )
                else Text(getString(R.string.items_title))
            },
            actions = {
                IconButton(
                    modifier = Modifier.testTag(StockPageTag.SearchButton),
                    onClick = { showSearchBar.value = !showSearchBar.value },
                ) {
                    Icon(
                        imageVector = searchButtonIcon,
                        contentDescription = "Search button"
                    )
                }

                if (!showSearchBar.value) {
                    IconButton(
                        modifier = Modifier.testTag(StockPageTag.LayoutButton),
                        onClick = { showGridLayout.value = !showGridLayout.value },
                    ) {
                        Icon(
                            imageVector = if (showGridLayout.value) Icons.Default.GridOff else Icons.Default.GridOn,
                            contentDescription = "Layout button"
                        )
                    }
                    IconButton(
                        modifier = Modifier.testTag(StockPageTag.FilterButton),
                        onClick = { showFilterDialog.value = !showFilterDialog.value },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Filter button"
                        )
                    }
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
                modifier = Modifier.testTag(StockPageTag.NewItemButton),
                text = { Text(text = "Neu") },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "New item FAB",
                    )
                },
                onClick = { showAddDialog.value = true },
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun buildContent(innerPadding: PaddingValues) {
        val stockModel by viewModel.stockModel.observeAsState(null)
        when {
            stockModel?.data?.isLoading != false -> LoadingIndicator()
            stockModel is Resource.Error -> ErrorScreen(stockModel!!.message!!.asString())
            else -> {
                val model = stockModel!!.data!!
                val stocks = model.stocks!!
                val items = model.items!!
                val categorys = items.keys.toList()
                val users = model.connectedUser ?: listOf()
                val user = model.user!!

                val pagerState = rememberPagerState { stocks.size }
                val allUser = users + listOf(user)

                Column(
                    verticalArrangement = Arrangement.Top
                ) {
                    useAddItemDialog(
                        showAddDialog,
                        categorys,
                        users,
                    ) { stockItem, item, close ->
                        viewModel.addItem(item, stockItem)
                        if (close) showAddDialog.value = false
                    }

                    TabRow(
                        containerColor = BaseColors.Black,
                        selectedTabIndex = pagerState.currentPage,
                    ) {
                        val scope = rememberCoroutineScope()
                        stocks.map { it.name }.forEachIndexed { index, title ->
                            Tab(
                                modifier = Modifier.testTag(StockPageTag.StockTabTag(title)),
                                text = { Text(title) },
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            )
                        }
                    }
                    HorizontalPager(state = pagerState) { page ->
                        val stock = stocks[page]
                        Column(
                            modifier = Modifier.testTag(StockPageTag.StockPage(stock.name)),
                            verticalArrangement = Arrangement.Top
                        ) {
                            val selectedUser = remember(stock) {
                                mutableStateOf(allUser.filter { stock.sharedWith.contains(it.uuid) })
                            }
                            buildUserDropDown(
                                "Lager wird nicht geteilt",
                                users,
                                selectedUser,
                                canChange = stock.creator == user.uuid,
                            ) {
                                viewModel.setSharedWith(stock, it)
                            }
                            if (items.isEmpty()) {
                                EmptyListComp(getString(R.string.no_items))
                            } else {
                                GridListLayout(
                                    innerPadding,
                                    showGridLayout,
                                    items,
                                    {
                                        stock.items.firstOrNull()?.color ?: BaseColors.LightGray
                                    }, //TODO color?
                                    viewModel::editCategory
                                ) { _, item ->
                                    listItem(
                                        stock.items.firstOrNull { it.uuid == item.uuid }
                                            ?: item.toStockItem(),
                                        item,
                                        categorys,
                                        users,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun listItem(
        stockItem: StockItem,
        item: Item,
        categorys: List<String>,
        users: List<User>,
    ) {
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
            onSwipe = { viewModel.deleteItem(item, stockItem) },
            onLongClick = { showEditDialog.value = true },
        ) { stockItem(item, stockItem) }
    }

    @Composable
    private fun stockItem(item: Item, stockItem: StockItem) {
        val filter = viewModel.filterBy.collectAsState(null)
        val text = highlightedText(item.name, filter.value?.searchTerm ?: "")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(ItemTag(item.category, item.name)),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BaseColors.LightGray.copy(alpha = .1f))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Start
            )

            val errors = viewModel.itemErrorList.collectAsState(listOf())
            val color =
                if (errors.value.contains(item.uuid)) BaseColors.FireRed
                else BaseColors.White
            AddSubRow(
                stockItem.amount,
                color,
            ) { viewModel.changeItemAmount(item.uuid, it) }
        }
    }
}