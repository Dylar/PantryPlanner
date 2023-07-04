package de.bitb.pantryplaner.ui.comps

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bitb.pantryplaner.core.misc.formatted
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import java.lang.Double.max


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddSubRow(
    itemId: String,
    amount: Double,
    errors: State<List<String>>,
    onChange: (String, String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val color =
            if (errors.value.contains(itemId)) BaseColors.FireRed
            else BaseColors.White
        val amountState =
            remember { mutableStateOf(TextFieldValue(amount.formatted)) }
        val interactionSource = remember { MutableInteractionSource() }

        IconButton(
            modifier = Modifier.testTag("minusButton"), // TODO set testTag
            onClick = {
                val new = max(amount - 1, 0.0).formatted
                amountState.value = TextFieldValue(new, TextRange(new.length))
                onChange(itemId, new)
            },
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Minus button"
            )
        }
        BasicTextField(
            amountState.value,
            modifier = Modifier.padding(2.dp).width(60.dp)
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
                    onChange(itemId, it.text)
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

        IconButton(
            modifier = Modifier.testTag("plusButton"), // TODO set testTag
            onClick = {
                val new = (amount + 1).formatted
                amountState.value = TextFieldValue(new, TextRange(new.length))
                onChange(itemId, new)
            },
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Plus button"
            )
        }
    }
}