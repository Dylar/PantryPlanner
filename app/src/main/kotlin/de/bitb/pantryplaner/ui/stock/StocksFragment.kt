package de.bitb.pantryplaner.ui.stock

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Result
import de.bitb.pantryplaner.data.model.Filter
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.data.model.Stock
import de.bitb.pantryplaner.data.model.StockItem
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.data.model.groupByCategory
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.NaviEvent
import de.bitb.pantryplaner.ui.base.comps.DismissItem
import de.bitb.pantryplaner.ui.base.comps.EmptyListComp
import de.bitb.pantryplaner.ui.base.comps.ErrorScreen
import de.bitb.pantryplaner.ui.base.comps.FloatingExpandingButton
import de.bitb.pantryplaner.ui.base.comps.GridListLayout
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator
import de.bitb.pantryplaner.ui.base.comps.SearchBar
import de.bitb.pantryplaner.ui.base.comps.buildUserDropDown
import de.bitb.pantryplaner.ui.base.comps.onBack
import de.bitb.pantryplaner.ui.base.highlightedText
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.testTags.ItemTag
import de.bitb.pantryplaner.ui.base.testTags.StocksPageTag
import de.bitb.pantryplaner.ui.base.testTags.UnsharedIconTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import de.bitb.pantryplaner.ui.checklists.ChecklistsFragment
import de.bitb.pantryplaner.ui.comps.AddSubRow
import de.bitb.pantryplaner.ui.comps.buildBottomNavi
import de.bitb.pantryplaner.ui.dialogs.ConfirmDialog
import de.bitb.pantryplaner.ui.dialogs.FilterDialog
import de.bitb.pantryplaner.ui.dialogs.useAddItemDialog
import de.bitb.pantryplaner.ui.dialogs.useAddStockDialog
import de.bitb.pantryplaner.ui.dialogs.useEditItemDialog
import de.bitb.pantryplaner.ui.profile.ProfileFragment
import de.bitb.pantryplaner.ui.recipes.RecipesFragment
import de.bitb.pantryplaner.ui.settings.SettingsFragment
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StocksFragment : BaseFragment<StocksViewModel>() {

    companion object {
        val naviFromChecklists: NaviEvent = NaviEvent.Navigate(R.id.checklists_to_stocks)
        val naviFromRecipes: NaviEvent = NaviEvent.Navigate(R.id.recipes_to_stocks)
        val naviFromProfile: NaviEvent = NaviEvent.Navigate(R.id.profile_to_stocks)
        val naviFromSettings: NaviEvent = NaviEvent.Navigate(R.id.settings_to_stocks)
    }

    override val viewModel: StocksViewModel by viewModels()

    private lateinit var showGridLayout: MutableState<Boolean>
    private lateinit var showFilterDialog: MutableState<Boolean>
    private lateinit var showAddStockDialog: MutableState<Boolean>
    private lateinit var showAddItemDialog: MutableState<Boolean>
    private lateinit var showSearchBar: MutableState<Boolean>

    private val searchButtonIcon: ImageVector
        get() =
            if (showSearchBar.value) Icons.Default.Cancel
            else if (viewModel.filterBy.value.filterByTerm) Icons.Default.SavedSearch
            else Icons.Default.Search

    @Composable
    override fun screenContent() {
        showSearchBar = remember { mutableStateOf(false) }
        showGridLayout = remember { mutableStateOf(true) }
        showFilterDialog = remember { mutableStateOf(false) }
        showAddStockDialog = remember { mutableStateOf(false) }
        showAddItemDialog = remember { mutableStateOf(false) }

        val filter by viewModel.filterBy.collectAsState(Filter())
        onBack { onDismiss ->
            ConfirmDialog(
                "Änderungen verwerfen?",
                "Möchten Sie die Item Auswahl verwerfen?",
                onConfirm = { navController.popBackStack() },
                onDismiss = { onDismiss() },
            )
        }

        val modelResp by viewModel.stocksModel.observeAsState()
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar(filter) },
            content = { buildContent(it, modelResp) },
            floatingActionButton = { buildFab(modelResp) },
            bottomBar = {
                buildBottomNavi(
                    checklistsRoute = ChecklistsFragment.naviFromStocks,
                    recipesRoute = RecipesFragment.naviFromStocks,
                    profileRoute = ProfileFragment.naviFromStocks,
                    settingsRoute = SettingsFragment.naviFromStocks,
                )
            }
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
            modifier = Modifier.testTag(StocksPageTag.AppBar),
            title = {
                val isSearching by viewModel.isSearching.collectAsState(false)
                if (showSearchBar.value) SearchBar(
                    showSearchBar,
                    isSearching,
                    filter.searchTerm,
                    viewModel::search,
                )
                else Text(getString(R.string.stock_title))
            },
            actions = {
                IconButton(
                    modifier = Modifier.testTag(StocksPageTag.SearchButton),
                    onClick = { showSearchBar.value = !showSearchBar.value },
                ) {
                    Icon(
                        imageVector = searchButtonIcon,
                        contentDescription = "Search button"
                    )
                }

                if (!showSearchBar.value) {
                    IconButton(
                        modifier = Modifier.testTag(StocksPageTag.FilterButton),
                        onClick = { showFilterDialog.value = !showFilterDialog.value },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Filter button"
                        )
                    }
                    IconButton(
                        modifier = Modifier.testTag(StocksPageTag.LayoutButton),
                        onClick = { showGridLayout.value = !showGridLayout.value },
                    ) {
                        Icon(
                            imageVector = if (showGridLayout.value) Icons.Default.GridOff else Icons.Default.GridOn,
                            contentDescription = "Layout button"
                        )
                    }
                }
            }
        )
    }

    @Composable
    private fun buildFab(modelResp: Result<StocksModel>?) {
        FloatingExpandingButton {
            ExtendedFloatingActionButton(
                modifier = Modifier.testTag(StocksPageTag.NewStockButton),
                text = { Text(text = "Lager") },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "New stock FAB",
                    )
                },
                onClick = { showAddStockDialog.value = true },
            )
            if (modelResp !is Result.Error &&
                modelResp?.data?.isLoading == false &&
                modelResp.data.stocks?.isNotEmpty() != false
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                ExtendedFloatingActionButton(
                    modifier = Modifier.testTag(StocksPageTag.NewItemButton),
                    text = { Text(text = "Item") },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "New item FAB",
                        )
                    },
                    onClick = { showAddItemDialog.value = true },
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun buildContent(innerPadding: PaddingValues, modelResp: Result<StocksModel>?) {
        when {
            modelResp is Result.Error -> ErrorScreen(modelResp.message!!.asString())
            modelResp?.data?.isLoading != false -> LoadingIndicator()
            else -> {
                val model = modelResp.data
                val settings = model.settings!!
                val items = model.items!!
                val stocks = model.stocks!!
                val user = model.user!!
                val connectedUser = model.connectedUser!!
                val sharedUser = model.sharedUser!!

                useAddStockDialog(
                    showAddStockDialog,
                    connectedUser,
                    onEdit = { loc, close ->
                        viewModel.addStock(loc)
                        if (close) showAddStockDialog.value = false
                    },
                )

                Column(
                    verticalArrangement = Arrangement.Top
                ) {
                    if (stocks.isEmpty()) {
                        EmptyListComp(getString(R.string.no_stocks))
                        return
                    }

                    val pagerState = rememberPagerState { stocks.size }
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                    ) {
                        val scope = rememberCoroutineScope()
                        stocks.map { it.name }.forEachIndexed { index, title ->
                            Tab(
                                modifier = Modifier.testTag(StocksPageTag.StockTabTag(title)),
                                text = { Text(title) },
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                            )
                        }
                    }
                    HorizontalPager(state = pagerState) { page ->
                        val stock = stocks[page]
                        StockPage(
                            innerPadding,
                            settings,
                            stock,
                            items[stock.uuid].orEmpty(),
                            user,
                            connectedUser,
                            sharedUser[stock.uuid].orEmpty(),
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun StockPage(
        innerPadding: PaddingValues,
        settings: Settings,
        stock: Stock,
        items: List<Item>,
        user: User,
        connectedUser: List<User>,
        sharedUser: List<User>,
    ) {
        val categorys = items.map { it.category }.toList()
        useAddItemDialog(
            showAddItemDialog,
            categorys,
            connectedUser,
        ) { item, close ->
            viewModel.addItem(item)
            if (close) showAddItemDialog.value = false
        }
        Column(
            modifier = Modifier.testTag(StocksPageTag.StockPage(stock.name)),
            verticalArrangement = Arrangement.Top
        ) {
            val selectedUser = remember(stock) { mutableStateOf(sharedUser) }
            buildUserDropDown(
                "Lager wird nicht geteilt",
                connectedUser,
                selectedUser,
                canChange = stock.creator == user.uuid,
            ) {
                viewModel.setSharedWith(stock, it)
            }

            if (items.isEmpty()) {
                EmptyListComp(getString(R.string.no_items))
                return
            }

            GridListLayout(
                innerPadding,
                showGridLayout,
                items.groupByCategory,
                settings::categoryColor,
                viewModel::editCategory
            ) { _, item ->
                val color = settings.categoryColor(item)
                ListItem(
                    stock,
                    item,
                    categorys,
                    user,
                    connectedUser,
                    color,
                )
            }
        }
    }

    @Composable
    private fun ListItem(
        stock: Stock,
        item: Item,
        categorys: List<String>,
        user: User,
        users: List<User>,
        color: Color,
    ) {
        val stockItem = stock.items.firstOrNull { it.uuid == item.uuid }
            ?: item.toStockItem()
        val showActionDialog = remember { mutableStateOf(false) }
        val isShared = item.sharedWith(user.uuid)
        if (isShared) {
            useEditItemDialog(
                showActionDialog,
                item,
                categorys,
                users,
                user,
            ) { i, _ -> viewModel.editItem(i) }
        } else {
            if (showActionDialog.value) {
                ConfirmDialog(
                    "Item hinzufügen",
                    "Möchten Sie das Item ihrem Item-Pool hinzufügen?",
                    onConfirm = {
                        showActionDialog.value = false
                        viewModel.shareItem(item)
                    },
                    onDismiss = { showActionDialog.value = false },
                )
            }
        }

        DismissItem(
            item.name,
            color,
            onSwipe = { viewModel.deleteItem(item) },
            onLongClick = { showActionDialog.value = true },
        ) { StockItem(isShared, stock, item, stockItem) }
    }

    @Composable
    private fun StockItem(isShared: Boolean, stock: Stock, item: Item, stockItem: StockItem) {
        val filter = viewModel.filterBy.collectAsState(null)
        val text = highlightedText(
            item.name,
            filter.value?.searchTerm ?: "",
            BaseColors.AdultBlue,
            BaseColors.SunYellow,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(ItemTag(item.name, item.category)),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start
                )
                if (!isShared)
                    Icon(
                        Icons.Filled.LinkOff,
                        modifier = Modifier
                            .testTag(UnsharedIconTag)
                            .padding(4.dp)
                            .size(18.dp),
                        contentDescription = null,
                    )
            }

            AddSubRow(stockItem.amount) { viewModel.changeItemAmount(stock, stockItem, it) }
        }
    }
}