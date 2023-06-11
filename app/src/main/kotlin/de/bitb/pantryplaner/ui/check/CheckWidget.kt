package de.bitb.pantryplaner.ui.check

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
            items = itemRepo.getLiveCheckList().first().data!!
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
    private fun CheckListContent(context: Context, items: List<Item>) {
        Box(
            modifier = GlanceModifier
                .padding(2.dp)
                .background(BaseColors.LightGray.copy(alpha = .2f))
        ) {
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) { items(items.size) { CheckListItem(context, items[it]) } }
        }
    }

    @Composable
    private fun CheckListItem(context: Context, item: Item) {
        fun onTap() = CoroutineScope(Dispatchers.IO).launch {
            itemUCs.checkItemUC(item)
            refresh(context)
        }
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(2.dp)
                .clickable { onTap() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CheckBox(
                item.checked,
                onCheckedChange = ::onTap,
                colors = CheckboxDefaults.colors(
                    checkedColor = item.color,
                    uncheckedColor = item.color
                )
            )
            Text(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .background(BaseColors.LightGray.copy(alpha = .3f)),
                text = item.name,
                style = TextDefaults.defaultTextStyle.copy(
                    color = ColorProvider(BaseColors.White),
                    textDecoration = if (item.checked) TextDecoration.LineThrough else TextDecoration.None
                )
            )
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
                .clickable { onSelect(color) },
            style = TextDefaults.defaultTextStyle.copy(color = ColorProvider(color))
        )
    }
}

