package de.bitb.pantryplaner.ui.intro

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.NotifyManager.ACTION_REFRESH_PAGE
import de.bitb.pantryplaner.core.misc.Logger
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.comps.LoadingIndicator

@AndroidEntryPoint
class SplashFragment : BaseFragment<SplashViewModel>() {
    override val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val naviToRefresh = activity?.intent?.action == ACTION_REFRESH_PAGE
        viewModel.loadData(naviToRefresh)
    }

    @Composable
    override fun screenContent() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(R.drawable.ic_launcher),
                contentDescription = "Launcher icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(220.dp)
                    .height(220.dp)
                    .clip(shape = CircleShape)
            )
            LoadingIndicator(
                modifier = Modifier
                    .height(100.dp)
                    .padding(top = 16.dp)
            )
        }
    }

}