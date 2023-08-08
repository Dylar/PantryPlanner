package de.bitb.pantryplaner.ui.intro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.TestTags
import de.bitb.pantryplaner.ui.base.styles.BaseColors
import de.bitb.pantryplaner.usecase.user.RegisterResponse

@AndroidEntryPoint
class RegisterFragment : BaseFragment<RegisterViewModel>() {

    override val viewModel: RegisterViewModel by viewModels()

    @Composable
    override fun screenContent() {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { buildAppbar() },
            floatingActionButton = { buildFAB() }
        ) { innerPadding -> buildContent(innerPadding) }
    }

    @Composable
    private fun buildAppbar() {
        TopAppBar(
            modifier = Modifier.testTag(TestTags.RegisterPage.AppBar.name),
            title = { Text(getString(R.string.register_title)) })
    }

    @Composable
    private fun buildFAB() {
        FloatingActionButton(
            modifier = Modifier
                .padding(all = 32.dp)
                .testTag(TestTags.RegisterPage.RegisterButton.name),
            onClick = { viewModel.register() }
        ) { Icon(Icons.Default.ArrowForward, contentDescription = "Register") }
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
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                    .testTag(TestTags.RegisterPage.FirstNameLabel.name),
                value = viewModel.firstName,
                onValueChange = { viewModel.firstName = it },
                label = { Text(getString(R.string.first_name)) },
            )
            OutlinedTextField(
                isError = viewModel.error == RegisterResponse.LastNameEmpty,
                singleLine = true,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .testTag(TestTags.RegisterPage.LastNameLabel.name),
                value = viewModel.lastName,
                onValueChange = { viewModel.lastName = it },
                label = { Text(getString(R.string.last_name)) }
            )
            OutlinedTextField(
                isError = viewModel.error is RegisterResponse.EmailError,
                singleLine = true,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .testTag(TestTags.RegisterPage.EmailLabel.name),
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text(getString(R.string.email)) }
            )
            OutlinedTextField(
                isError = viewModel.error is RegisterResponse.PWError,
                singleLine = true,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .testTag(TestTags.RegisterPage.PW1Label.name),
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
                    .testTag(TestTags.RegisterPage.PW2Label.name),
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
                        .testTag(TestTags.RegisterPage.ErrorLabel.name),
                    contentAlignment = Alignment.TopCenter,
                ) { Text(it.message.asString(), color = BaseColors.FireRed) }
            }
            Spacer(modifier = Modifier.padding(top = 100.dp))
        }
    }
}