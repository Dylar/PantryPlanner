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
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bitb.pantryplaner.core.misc.Logger
import de.bitb.pantryplaner.core.misc.formatted
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.testTags.AddSubRowTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import java.lang.Double.max

@Composable
fun AddSubRow(
    amount: Double,
    onChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val amountState =
            remember {
                val value = amount.formatted
                mutableStateOf(TextFieldValue(value, selection = TextRange(value.length)))
            }

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
                modifier = Modifier.background(BaseColors.Red, shape = CircleShape),
                imageVector = Icons.Default.Remove,
                contentDescription = "Minus button"
            )
        }
        EditText(amountState) { onChange(it) }

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
                modifier = Modifier.background(BaseColors.Green, shape = CircleShape),
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
    onChange: (String) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        textState.value,
        modifier = Modifier
            .testTag(AddSubRowTag.AmountText)
            .padding(2.dp)
            .width(60.dp)
            .background(MaterialTheme.colors.background),
        textStyle = TextStyle.Default.copy(
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground,
        ),
        maxLines = 1,
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = { field ->
            Logger.printLog("value" to field.text)
            val sanitizedInput = field.text.replace(",", ".")
            val pattern = "^\\d{0,5}(\\.\\d{0,2})?$|^.\\d{1,2}$".toRegex()
            if (sanitizedInput.matches(pattern)) {
                textState.value = TextFieldValue(sanitizedInput, field.selection)
                onChange(sanitizedInput)
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
