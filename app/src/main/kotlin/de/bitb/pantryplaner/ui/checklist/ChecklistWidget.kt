package de.bitb.pantryplaner.ui.checklist

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextDefaults
import androidx.glance.unit.ColorProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import de.bitb.pantryplaner.data.ItemRepository
import de.bitb.pantryplaner.data.model.Item
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.styles.BaseColors.FilterColors
import de.bitb.pantryplaner.usecase.ItemUseCases
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ScreenWidgetEntryPoint {
    fun getRepo(): ItemRepository
    fun getUCs(): ItemUseCases
}

class WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: ScreenWidget = ScreenWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        glanceAppWidget.refresh(context)
    }
}

class ScreenWidget : GlanceAppWidget() {
    private val coroutineScope: CoroutineScope = MainScope()

    private lateinit var dependencies: ScreenWidgetEntryPoint

    private val itemRepo: ItemRepository
        get() = dependencies.getRepo()

    private val itemUCs: ItemUseCases
        get() = dependencies.getUCs()

    private var items by mutableStateOf<List<Item>>(emptyList())
    private val filterBy = mutableStateOf(FilterColors.first())

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)
        coroutineScope.cancel()
    }

    fun refresh(context: Context) {
        coroutineScope.launch {
            dependencies =
                EntryPointAccessors.fromApplication(context, ScreenWidgetEntryPoint::class.java)
            items = itemRepo.getItems().first().data!!
            updateAll(context)
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        refresh(context)
        provideContent {
            val showItems =
                if (filterBy.value == FilterColors.first()) items else items.filter { filterBy.value == it.color }

            Column(modifier = GlanceModifier.fillMaxSize()) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .background(BaseColors.Black)
                ) { ColorRow(context, filterBy, FilterColors) }
                when {
                    showItems.isEmpty() -> EmptyListContent()
                    else -> CheckListContent(context, showItems)
                }
            }
        }
    }

    @Composable
    private fun EmptyListContent() {
        Box(
            modifier = GlanceModifier
                .padding(2.dp)
                .fillMaxSize()
                .background(BaseColors.LightGray.copy(alpha = .2f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = GlanceModifier.fillMaxSize(),
                text = "No items",
                style = TextDefaults.defaultTextStyle.copy(
                    color = ColorProvider(BaseColors.White),
                    textAlign = TextAlign.Center
                )
            )
        }
    }

    @Composable
    private fun CheckListContent(context: Context, itemsList: List<Item>) {
        val renderItems = itemsList.groupBy { it.category }
        Box(
            modifier = GlanceModifier
                .padding(2.dp)
                .background(BaseColors.LightGray.copy(alpha = .2f))
        ) {
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                renderItems.forEach { (header, list) ->
                    if (header.isNotBlank()) {
                        item {
                            Box(
                                GlanceModifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 2.dp)
                                    .background(BaseColors.Black.copy(alpha = .4f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    header,
                                    modifier = GlanceModifier
                                        .padding(4.dp)
                                        .background(BaseColors.LightGray.copy(alpha = .4f)),
                                    style = TextDefaults.defaultTextStyle.copy(
                                        color = ColorProvider(BaseColors.White),
                                        fontSize = 10.sp,
                                        textDecoration = TextDecoration.Underline
                                    ),
                                )
                            }
                        }
                    }
                    items(list.size) {
                        CheckListItem(context, list[it])
                    }
                }
            }
        }
    }

    @Composable
    private fun CheckListItem(context: Context, item: Item) {
        fun onTap() = CoroutineScope(Dispatchers.IO).launch {
//            itemUCs.checkItemUC(item) //TODO repair widget
            refresh(context)
        }
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(2.dp)
                .background(BaseColors.LightGray.copy(alpha = .3f))
                .clickable { onTap() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CheckBox(
                false, //TODO repair widget
                modifier = GlanceModifier,
                onCheckedChange = ::onTap,
                colors = CheckboxDefaults.colors(
                    checkedColor = item.color,
                    uncheckedColor = item.color
                )
            )
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
            {
                if (item.category.isNotBlank()) {
                    Text(
                        item.category,
                        modifier = GlanceModifier
                            .padding(2.dp),
                        style = TextDefaults.defaultTextStyle.copy(
                            color = ColorProvider(BaseColors.White),
                            fontSize = 10.sp,
                        )
                    )
                }
                Text(
                    modifier = GlanceModifier
                        .padding(start = 2.dp),
                    text = item.name,
                    style = TextDefaults.defaultTextStyle.copy(
                        color = ColorProvider(BaseColors.White),
                        fontSize = 16.sp,
                        //TODO repair widget
//                        textDecoration = if (item.checked) TextDecoration.LineThrough else TextDecoration.None
                    )
                )
            }
        }
    }

    @Composable
    fun ColorRow(
        context: Context,
        selectedCircleIndex: MutableState<Color>,
        selectableColors: List<Color>
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .background(BaseColors.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (index in selectableColors.indices) {
                val color = selectableColors[index]
                ColorDongle(
                    index,
                    color = color,
                    isSelected = color == selectedCircleIndex.value,
                ) {
                    selectedCircleIndex.value = color
                    refresh(context)
                }
            }
        }
    }

    @Composable
    fun ColorDongle(
        index: Int,
        color: Color,
        isSelected: Boolean,
        onSelect: (Color) -> Unit,
    ) {
        Text(
            index.toString(),
            modifier = GlanceModifier
                .background(if (isSelected) BaseColors.Black.copy(alpha = .4f) else Color.Transparent)
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .cornerRadius(30.dp)
                .clickable { onSelect(color) },
            style = TextDefaults.defaultTextStyle.copy(color = ColorProvider(color))
        )
    }
}