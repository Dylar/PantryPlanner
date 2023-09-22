package de.bitb.pantryplaner.ui.intro

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.ui.base.testTags.RegisterPageTag
import de.bitb.pantryplaner.ui.base.testTags.testTag
import de.bitb.pantryplaner.usecase.user.RegisterResponse

@AndroidEntryPoint
class RegisterFragment : BaseFragment<RegisterViewModel>() {

    override val viewModel: RegisterViewModel by viewModels()

    @Composable
    override fun screenContent() {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppbar() },
            floatingActionButton = { buildFab() }
        ) { innerPadding -> buildContent(innerPadding) }
    }

    @Composable
    private fun buildAppbar() {
        TopAppBar(
            modifier = Modifier.testTag(RegisterPageTag.AppBar),
            title = { Text(getString(R.string.register_title)) })
    }

    @Composable
    private fun buildFab() {
        FloatingActionButton(
            modifier = Modifier
                .padding(all = 32.dp)
                .testTag(RegisterPageTag.RegisterButton),
            onClick = { viewModel.register() }
        ) {
            if (viewModel.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
            else Icon(Icons.Default.ArrowForward, contentDescription = "Register")
        }
    }

    @Composable
    private fun buildContent(innerPadding: PaddingValues) {
        var password1Visibility by remember { mutableStateOf(false) }
        var password2Visibility by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                isError = viewModel.error == RegisterResponse.FirstNameEmpty,
                singleLine = true,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .testTag(RegisterPageTag.FirstNameLabel),
                value = viewModel.firstName,
                onValueChange = { viewModel.firstName = it },
                label = { Text(getString(R.string.first_name)) },
            )
            OutlinedTextField(
                isError = viewModel.error == RegisterResponse.LastNameEmpty,
                singleLine = true,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .testTag(RegisterPageTag.LastNameLabel),
                value = viewModel.lastName,
                onValueChange = { viewModel.lastName = it },
                label = { Text(getString(R.string.last_name)) }
            )
            OutlinedTextField(
                isError = viewModel.error is RegisterResponse.EmailError,
                singleLine = true,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .testTag(RegisterPageTag.EmailLabel),
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text(getString(R.string.email)) }
            )
            OutlinedTextField(
                isError = viewModel.error is RegisterResponse.PWError,
                singleLine = true,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .testTag(RegisterPageTag.PW1Label),
                value = viewModel.pw1,
                onValueChange = { viewModel.pw1 = it },
                label = { Text(getString(R.string.pw1_label)) },
                visualTransformation = if (password1Visibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(
                        onClick = { password1Visibility = !password1Visibility },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        val icon =
                            if (password1Visibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        Icon(icon, contentDescription = "Toggle password visibility")
                    }
                },
            )
            OutlinedTextField(
                isError = viewModel.error is RegisterResponse.PWError,
                singleLine = true,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .testTag(RegisterPageTag.PW2Label),
                value = viewModel.pw2,
                onValueChange = { viewModel.pw2 = it },
                label = { Text(getString(R.string.pw2_label)) },
                visualTransformation = if (password2Visibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(
                        onClick = { password2Visibility = !password2Visibility },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        val icon =
                            if (password2Visibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        Icon(icon, contentDescription = "Toggle password visibility")
                    }
                },
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            viewModel.error?.let {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .testTag(RegisterPageTag.ErrorLabel),
                    contentAlignment = Alignment.TopCenter,
                ) { Text(it.message.asString(), color = BaseColors.FireRed) }
            }
            Spacer(modifier = Modifier.padding(top = 100.dp))
        }
    }
}