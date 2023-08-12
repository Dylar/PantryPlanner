package de.bitb.pantryplaner.ui.profile

import android.util.Log
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.viewModels
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.TestTags
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator
import de.bitb.pantryplaner.ui.base.naviToSettings

@AndroidEntryPoint
class ProfileFragment : BaseFragment<ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()

    @Composable
    override fun screenContent() {
        val user by viewModel.user.observeAsState(null)
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            content = {
                when {
                    user != null -> UserDetails(it, user!!)
                    else -> LoadingIndicator()
                }
            },
        )
    }

    @Composable
    private fun buildAppBar() {
        TopAppBar(
            modifier = Modifier.testTag(TestTags.ProfilePage.AppBar.name),
            title = { Text(getString(R.string.profile_title)) },
            actions = {
                IconButton(
                    onClick = ::naviToSettings,
                    modifier = Modifier.testTag(TestTags.ProfilePage.SettingsButton.name)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "settings button"
                    )
                }
            },
        )
    }

    @Composable
    fun UserDetails(padding: PaddingValues, user: User) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .testTag(TestTags.ProfilePage.QRInfo.name),
                    contentAlignment = Alignment.Center,
                ) { Text(getString(R.string.profile_qr_info)) }
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) { QrCodeImage(user.uuid) }
            }
        }
    }

    @Composable
    fun QrCodeImage(uuid: String) {
        val black = MaterialTheme.colors.background
        val white = MaterialTheme.colors.onBackground
        return AndroidView(
            modifier = Modifier.testTag(TestTags.ProfilePage.QRLabel.name),
            factory = { context ->
                ImageView(context).apply {
                    QRGEncoder(uuid, null, QRGContents.Type.TEXT, 800).apply {
                        colorBlack = black.toArgb()
                        colorWhite = white.toArgb()
                        try {
                            setImageBitmap(bitmap)
                        } catch (e: WriterException) {
                            Log.e(toString(), e.toString())
                        }
                    }
                }
            }
        )
    }
}
