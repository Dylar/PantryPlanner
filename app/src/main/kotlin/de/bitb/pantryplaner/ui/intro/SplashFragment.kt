package de.bitb.pantryplaner.ui.intro

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.composable.LoadingIndicator

@AndroidEntryPoint
class SplashFragment : BaseFragment<SplashViewModel>() {
    override val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadData()
    }

    @Composable
    override fun ScreenContent() = LoadingIndicator()

}