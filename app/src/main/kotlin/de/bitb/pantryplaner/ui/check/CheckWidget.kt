package de.bitb.pantryplaner.ui.check

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.lazy.LazyColumn
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
            Column(modifier = GlanceModifier.fillMaxSize()) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .background(BaseColors.Black)
                ) {
                    Button(
                        modifier = GlanceModifier.fillMaxWidth().padding(2.dp),
                        text = "Refresh",
                        style = TextDefaults.defaultTextStyle.copy(
                            color = ColorProvider(BaseColors.White),
                            textAlign = TextAlign.Center,
                        ),
                        colors = ButtonDefaults.buttonColors(
                            ColorProvider(BaseColors.LightGreen),
                            ColorProvider(BaseColors.Black)
                        ),
                        onClick = { refresh(context) }
                    )
                }
                when {
                    items.isEmpty() -> EmptyListContent()
                    else -> CheckListContent(context)
                }
            }
        }
    }

    @Composable
    private fun EmptyListContent() {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) { Text(text = "No items") }
    }

    @Composable
    private fun CheckListContent(context: Context) {
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
}
