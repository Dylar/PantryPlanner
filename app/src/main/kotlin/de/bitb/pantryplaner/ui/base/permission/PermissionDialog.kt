package de.bitb.pantryplaner.ui.base.permission

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.ui.base.comps.ResString
import de.bitb.pantryplaner.ui.base.permission.PermissionDialogTags.PERMISSION_DIALOG

object PermissionDialogTags {
    const val PERMISSION_DIALOG = "PermissionDialog"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier.testTag(PERMISSION_DIALOG),
        onDismissRequest = onDismiss,
        content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.permission_needed))
                Text(
                    text = permissionTextProvider
                        .getDescription(isPermanentlyDeclined = isPermanentlyDeclined)
                        .asString()
                )
                Divider()
                Text(
                    text = buttonText(isPermanentlyDeclined).asString(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { if (isPermanentlyDeclined) onGoToAppSettingsClick() else onOkClick() }
                        .padding(16.dp)
                )
            }
        },
    )
}

private fun buttonText(isPermanentlyDeclined: Boolean): ResString = ResString.ResourceString(
    if (isPermanentlyDeclined) R.string.grant_permission
    else R.string.ok
)

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): ResString
}

class CameraPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): ResString =
        ResString.ResourceString(
            if (isPermanentlyDeclined) R.string.camera_permission_declined
            else R.string.camera_permission_asking
        )
}