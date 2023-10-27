package de.bitb.pantryplaner.ui.profile

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.comps.LifecycleComp
import de.bitb.pantryplaner.ui.base.openAppSettings
import de.bitb.pantryplaner.ui.base.permission.CameraPermissionTextProvider
import de.bitb.pantryplaner.ui.base.permission.PermissionDialog
import de.bitb.pantryplaner.ui.base.permission.PermissionHandler
import de.bitb.pantryplaner.ui.base.permission.PermissionHandlerImpl
import de.bitb.pantryplaner.ui.base.testTags.ScanPageTag
import de.bitb.pantryplaner.ui.base.testTags.testTag

@AndroidEntryPoint
class ScanFragment : BaseFragment<ScanViewModel>(), PermissionHandler by PermissionHandlerImpl() {

    override val viewModel: ScanViewModel by viewModels()

    @Composable
    override fun screenContent() {
        val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                onPermissionResult(
                    permission = Manifest.permission.CAMERA,
                    isGranted = isGranted
                )
            }
        )

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(ScanPageTag.AppBar),
                    title = { Text(stringResource(R.string.scan_title)) }
                )
            },
        ) { innerPadding ->
            ScannerPreview(innerPadding)
            cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
        }

        visiblePermissionDialogQueue
            .reversed()
            .forEach { permission ->
                PermissionDialog(
                    permissionTextProvider = when (permission) {
                        Manifest.permission.CAMERA -> CameraPermissionTextProvider()
                        else -> return@forEach
                    },
                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission),
                    onDismiss = ::dismissPermissionDialog,
                    onOkClick = ::dismissPermissionDialog,
                    onGoToAppSettingsClick = { activity?.openAppSettings() },
                )
            }
    }

    @Composable
    fun ScannerPreview(innerPadding: PaddingValues) {
        var codeScanner: CodeScanner? = null
        AndroidView(
            modifier = Modifier
                .padding(innerPadding)
                .testTag(ScanPageTag.ScanLabel),
            factory = { context ->
                CodeScannerView(context).apply {
                    isMaskVisible = true
                    CodeScanner(context, this).apply {
                        codeScanner = this
                        camera =
                            CodeScanner.CAMERA_BACK
                        formats = CodeScanner.ALL_FORMATS
                        autoFocusMode = AutoFocusMode.SAFE
                        scanMode = ScanMode.SINGLE
                        isAutoFocusEnabled = true
                        isFlashEnabled = false
                        errorCallback =
                            ErrorCallback { Log.e(this@ScanFragment.tag, it.toString()) }
                        decodeCallback = DecodeCallback { viewModel.onScan(it.text) }
                        setOnClickListener { startPreview() }
                        startPreview()
                    }
                }
            },
        )
        LifecycleComp { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> codeScanner?.startPreview()
                Lifecycle.Event.ON_PAUSE -> codeScanner?.releaseResources()
                else -> {}
            }
        }
    }
}