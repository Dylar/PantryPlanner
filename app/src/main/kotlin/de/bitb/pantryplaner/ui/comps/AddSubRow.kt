package de.bitb.pantryplaner.ui.comps

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import de.bitb.pantryplaner.ui.base.testTags.AddSubRowTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import java.lang.Double.max

@Composable
fun AddSubRow(
    amount: Double,
    editColor: Color = BaseColors.White,
    backgroundColor: Color = Color.Transparent,
    onChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val amountState =
            remember { mutableStateOf(TextFieldValue(amount.formatted)) }

        IconButton(
            modifier = Modifier
                .testTag(AddSubRowTag.MinusButton)
                .size(48.dp),
            onClick = {
                val new = max(amount - 1, 0.0).formatted
                amountState.value = TextFieldValue(new, TextRange(new.length))
                onChange(new)
            },
        ) {
            Icon(
                modifier = Modifier
                    .background(Color.Red, shape = CircleShape),
                imageVector = Icons.Default.Remove,
                contentDescription = "Minus button"
            )
        }
        EditText(amountState, editColor) { onChange(it) }

        IconButton(
            modifier = Modifier
                .testTag(AddSubRowTag.PlusButton)
                .size(48.dp),
            onClick = {
                val new = (amount + 1).formatted
                amountState.value = TextFieldValue(new, TextRange(new.length))
                onChange(new)
            },
        ) {
            Icon(
                modifier = Modifier
                    .background(Color.Green, shape = CircleShape),
                imageVector = Icons.Default.Add,
                contentDescription = "Plus button"
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditText(
    textState: MutableState<TextFieldValue>,
    color: Color = BaseColors.White,
    onChange: (String) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        textState.value,
        modifier = Modifier
            .padding(2.dp)
            .width(60.dp)
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
                textState.value = it
                onChange(it.text)
            }
        },
    ) { innerTextField ->
        TextFieldDefaults.TextFieldDecorationBox(
            value = textState.value.text,
            visualTransformation = VisualTransformation.None,
            innerTextField = innerTextField,
            singleLine = true,
            enabled = true,
            interactionSource = interactionSource,
            contentPadding = PaddingValues(0.dp),
        )
    }
}
