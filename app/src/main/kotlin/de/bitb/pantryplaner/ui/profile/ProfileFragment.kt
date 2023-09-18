package de.bitb.pantryplaner.ui.profile

import android.util.Log
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.viewModels
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.TestTags
import de.bitb.pantryplaner.ui.base.comps.*
import de.bitb.pantryplaner.ui.base.naviToScan
import de.bitb.pantryplaner.ui.base.naviToSettings
import de.bitb.pantryplaner.ui.base.styles.BaseColors

@AndroidEntryPoint
class ProfileFragment : BaseFragment<ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()

    @Composable
    override fun screenContent() {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            floatingActionButton = { buildFab() },
            content = { buildContent(it) },
        )
    }

    @Composable
    private fun buildContent(paddingValues: PaddingValues) {
        val user by viewModel.user.observeAsState(null)
        when {
            user is Resource.Error -> ErrorScreen(user!!.message!!.asString())
            user == null || !user!!.hasData -> LoadingIndicator()
            else -> UserDetails(paddingValues, user!!.data!!)
        }
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
    private fun buildFab() {
        FloatingActionButton(
            modifier = Modifier.testTag(TestTags.ProfilePage.ScanButton.name),
            onClick = ::naviToScan // TODO add via (email)dialog
        ) { Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan Buddy") }
    }

    @Composable
    private fun UserDetails(padding: PaddingValues, user: User) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) { QrCodeImage(user.uuid) }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .testTag(TestTags.ProfilePage.QRInfo.name),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        getString(R.string.profile_qr_info),
                        modifier = Modifier
                            .drawBehind {
                                val strokeWidthPx = 1.dp.toPx()
                                val verticalOffset = size.height - 2.sp.toPx()
                                drawLine(
                                    color = BaseColors.ZergPurple,
                                    strokeWidth = strokeWidthPx,
                                    start = Offset(0f, verticalOffset),
                                    end = Offset(size.width, verticalOffset)
                                )
                            },
                        textAlign = TextAlign.Center,
                    )
                }
                ConnectedUserList()
            }
        }
    }

    @Composable
    private fun QrCodeImage(uuid: String) {
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

    @Composable
    private fun ConnectedUserList() {
        val connectedResp by viewModel.connectedUser.observeAsState()
        when {
            connectedResp is Resource.Error -> ErrorScreen(connectedResp!!.message!!.asString())
            connectedResp == null || !connectedResp!!.hasData -> LoadingIndicator()
            else -> {
                val connectedUser = (connectedResp as Resource<List<User>>).data!!
                LazyVerticalGrid(
                    GridCells.Fixed(if (connectedUser.size == 1) 1 else 2),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalArrangement = Arrangement.Center,
                    contentPadding = PaddingValues(horizontal = 20.dp),
                ) {
                    if (connectedUser.isNotEmpty()) {
                        stickyGridHeader {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Text(
                                    "Verbundene Benutzer",
                                    modifier = Modifier
                                        .drawBehind {
                                            val strokeWidthPx = 1.dp.toPx()
                                            val verticalOffset = size.height - 2.sp.toPx()
                                            drawLine(
                                                color = BaseColors.ZergPurple,
                                                strokeWidth = strokeWidthPx,
                                                start = Offset(0f, verticalOffset),
                                                end = Offset(size.width, verticalOffset)
                                            )
                                        },
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                    connectedUser.forEach { user -> items(connectedUser.size) { buildUser(user) } }
                }
            }
        }
    }

    @Composable
    private fun buildUser(user: User) {
        dissmissItem(
            user.fullName,
            BaseColors.ZergPurple,
            onSwipe = { viewModel.disconnectUser(user) },
        ) {
            Box(
                modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    user.fullName,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}
