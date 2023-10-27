package de.bitb.pantryplaner.ui.intro

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.naviLoginToReleaseNotes
import de.bitb.pantryplaner.ui.base.naviToRegister
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.testTags.LoginPageTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import de.bitb.pantryplaner.ui.dialogs.InfoDialog
import de.bitb.pantryplaner.usecase.user.LoginResponse

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel>() {

    override val viewModel: LoginViewModel by viewModels()

    @Composable
    override fun screenContent() {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppBar() },
            floatingActionButton = { buildFab() }
        ) { innerPadding -> buildContent(innerPadding) }
    }

    @Composable
    private fun buildAppBar() {
        var showDialog by remember { mutableStateOf(false) }
        if (showDialog) {
            InfoDialog(::naviLoginToReleaseNotes) { showDialog = false }
        }
        TopAppBar(
            modifier = Modifier.testTag(LoginPageTag.AppBar),
            title = { Text(getString(R.string.login_title)) },
            actions = {
                IconButton(
                    modifier = Modifier.testTag(LoginPageTag.InfoButton),
                    onClick = { showDialog = !showDialog }
                ) { Icon(Icons.Default.Info, contentDescription = "Info") }
            }
        )
    }

    @Composable
    private fun buildFab() {
        FloatingActionButton(
            modifier = Modifier
                .padding(all = 32.dp)
                .testTag(LoginPageTag.LoginButton),
            onClick = { viewModel.login() },
        ) {
            if (viewModel.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
            else Icon(Icons.Default.ArrowForward, contentDescription = "Login")
        }
    }

    @Composable
    private fun buildContent(innerPadding: PaddingValues) {
        var passwordVisibility by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                isError = viewModel.error is LoginResponse.EmailError,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .testTag(LoginPageTag.EmailLabel),
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text(getString(R.string.email)) },
            )
            OutlinedTextField(
                isError = viewModel.error is LoginResponse.PwEmpty,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .testTag(LoginPageTag.PWLabel),
                value = viewModel.pw,
                onValueChange = { viewModel.pw = it },
                label = { Text(getString(R.string.pw1_label)) },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisibility = !passwordVisibility },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        val icon =
                            if (passwordVisibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        Icon(icon, contentDescription = "Toggle password visibility")
                    }
                },
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            viewModel.error?.let {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .testTag(LoginPageTag.ErrorLabel),
                    contentAlignment = Alignment.TopCenter,
                ) { Text(it.message.asString(), color = BaseColors.FireRed) }
            }
            Button(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .testTag(LoginPageTag.RegisterButton),
                onClick = ::naviToRegister,
                content = {
                    Text(
                        text = getString(R.string.login_register_account),
                        textAlign = TextAlign.Center,
                    )
                },
            )
        }
    }
}