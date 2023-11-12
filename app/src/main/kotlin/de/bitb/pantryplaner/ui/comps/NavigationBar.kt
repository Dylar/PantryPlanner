package de.bitb.pantryplaner.ui.comps

import androidx.annotation.IdRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.HomeWork
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.NaviEvent
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.testTags.BottomNaviTag
import de.bitb.pantryplaner.ui.base.testTags.TestTag
import de.bitb.pantryplaner.ui.base.testTags.testTag

@Composable
fun BaseFragment<*>.buildBottomNavi(
    @IdRes checklistsRoute: Int? = null,
    @IdRes stocksRoute: Int? = null,
    @IdRes profileRoute: Int? = null,
    @IdRes settingsRoute: Int? = null,
) {
    BottomNavigationBar(
        listOf(
            BottomItemInfo(
                BottomNaviTag.ChecklistsButton,
                stringResource(id = R.string.checklists_title),
                Icons.Rounded.Checklist,
                checklistsRoute?.let { NaviEvent.Navigate(it) },
            ),
            BottomItemInfo(
                BottomNaviTag.StocksButton,
                stringResource(id = R.string.stock_title),
                Icons.Rounded.HomeWork,
                stocksRoute?.let { NaviEvent.Navigate(it) },
            ),
            BottomItemInfo(
                BottomNaviTag.ProfileButton,
                stringResource(id = R.string.profile_title),
                Icons.Rounded.Person,
                profileRoute?.let { NaviEvent.Navigate(it) },
            ),
            BottomItemInfo(
                BottomNaviTag.SettingsButton,
                stringResource(id = R.string.settings_title),
                Icons.Rounded.Settings,
                settingsRoute?.let { NaviEvent.Navigate(it) },
            ),
        )
    )
}

data class BottomItemInfo(
    val testTag: TestTag,
    val title: String,
    val icon: ImageVector,
    val navigateEvent: NaviEvent? = null,
)

@Composable
fun BaseFragment<*>.BottomNavigationBar(items: List<BottomItemInfo>) {
    BottomNavigation {
        items.forEach { screen ->
            val isSelected = screen.navigateEvent == null
            BottomNavigationItem(
                modifier = Modifier.testTag(screen.testTag),
                icon = {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    if (isSelected) MaterialTheme.colors.onSurface else BaseColors.Transparent,
                                    shape = CircleShape
                                )
                        )
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = if (isSelected) MaterialTheme.colors.surface else MaterialTheme.colors.onSurface
                        )
                    }
                },
                label = { Text(screen.title) },
                selected = isSelected,
                onClick = { screen.navigateEvent?.let { viewModel.navigate(it) } }
            )
        }
    }
}
