package de.bitb.pantryplaner.ui.profile

import android.util.Log
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.HomeWork
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.viewModels
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.model.Location
import de.bitb.pantryplaner.data.model.User
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.comps.ErrorScreen
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator
import de.bitb.pantryplaner.ui.base.comps.dissmissItem
import de.bitb.pantryplaner.ui.base.comps.stickyGridHeader
import de.bitb.pantryplaner.ui.base.naviToScan
import de.bitb.pantryplaner.ui.base.naviToSettings
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.testTags.LocationItem
import de.bitb.pantryplaner.ui.base.testTags.ProfilePageTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import de.bitb.pantryplaner.ui.dialogs.useAddLocationDialog
import de.bitb.pantryplaner.ui.dialogs.useEditLocationDialog

@AndroidEntryPoint
class ProfileFragment : BaseFragment<ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()

    private lateinit var showAddDialog: MutableState<Boolean>

    @Composable
    override fun screenContent() {
        showAddDialog = remember { mutableStateOf(false) }
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            floatingActionButton = { buildFab() },
            content = { buildContent(it) },
        )
    }

    @Composable
    private fun buildContent(paddingValues: PaddingValues) {
        val model by viewModel.profileModel.observeAsState(null)
        when {
            model is Resource.Error -> ErrorScreen(model!!.message!!.asString())
            model?.data?.isLoading != false -> LoadingIndicator()
            else -> UserDetails(paddingValues, model!!.data!!)
        }
    }

    @Composable
    private fun buildAppBar() {
        TopAppBar(
            modifier = Modifier.testTag(ProfilePageTag.AppBar),
            title = { Text(getString(R.string.profile_title)) },
            actions = {
                IconButton(
                    onClick = ::naviToSettings,
                    modifier = Modifier.testTag(ProfilePageTag.SettingsButton)
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
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
        ) {
            ExtendedFloatingActionButton(
                modifier = Modifier.testTag(ProfilePageTag.NewLocationButton),
                onClick = { showAddDialog.value = true },
                text = { Text(text = "Ort anlegen") },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.HomeWork,
                        contentDescription = "Add Location",
                    )
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
            ExtendedFloatingActionButton(
                modifier = Modifier.testTag(ProfilePageTag.ScanButton),
                onClick = ::naviToScan, // TODO add via (email)dialog
                text = { Text(text = "Scannen") },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.QrCodeScanner,
                        contentDescription = "Scan",
                    )
                },
            )
        }
    }

    @Composable
    private fun UserDetails(padding: PaddingValues, model: ProfileModel) {
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
                ) { QrCodeImage(model.user!!.uuid) }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .testTag(ProfilePageTag.QRInfo),
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
                ConnectedUserList(model.connectedUser!!)
                LocationList(model.connectedUser, model.locations!!)
            }
        }
    }

    @Composable
    private fun QrCodeImage(uuid: String) {
        val black = MaterialTheme.colors.background
        val white = MaterialTheme.colors.onBackground
        return AndroidView(
            modifier = Modifier.testTag(ProfilePageTag.QRLabel),
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
    private fun ConnectedUserList(connectedUser: List<User>) {
        LazyVerticalGrid(
            GridCells.Fixed(if (connectedUser.size == 1) 1 else 2),
            modifier = Modifier.fillMaxWidth(),
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
            items(connectedUser.size) { buildUser(connectedUser[it]) }
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

    @Composable
    private fun LocationList(users: List<User>, locations: List<Location>) {
        useAddLocationDialog(
            showAddDialog,
            users,
            onEdit = { loc, close ->
                viewModel.addLocation(loc)
                if (close) showAddDialog.value = false
            },
        )
        LazyVerticalGrid(
            GridCells.Fixed(if (locations.size == 1) 1 else 2),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalArrangement = Arrangement.Center,
            contentPadding = PaddingValues(horizontal = 20.dp),
        ) {
            if (locations.isNotEmpty()) {
                stickyGridHeader {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            "Orte",
                            modifier = Modifier
                                .drawBehind { // TODO make generic
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
            items(locations.size) { buildLocation(users, locations[it]) }
        }
    }

    @Composable
    private fun buildLocation(users: List<User>, location: Location) {
        val showEditDialog = remember { mutableStateOf(false) }
        useEditLocationDialog(
            showDialog = showEditDialog,
            location = location,
            users = users,
            onEdit = { loc, _ ->
                showEditDialog.value = false
                viewModel.editLocation(loc)
            }
        )

        dissmissItem(
            location.name,
            BaseColors.ZergPurple,
            onSwipe = { viewModel.removeLocation(location) },
            onLongClick = { showEditDialog.value = true }
        ) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag(LocationItem(location.name)),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    location.name,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}
