package de.bitb.pantryplaner.ui.comps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bitb.pantryplaner.ui.base.styles.BaseColors

@Composable
fun DeleteItemBackground() {
    Card(
        elevation = 4.dp,
        modifier = Modifier
//            .fillMaxSize()
            .padding(vertical = 4.dp)
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
}