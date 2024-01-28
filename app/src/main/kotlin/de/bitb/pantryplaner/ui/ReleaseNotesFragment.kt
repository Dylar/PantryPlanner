package de.bitb.pantryplaner.ui

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.R
import de.bitb.pantryplaner.core.misc.readTextFromAsset
import de.bitb.pantryplaner.ui.base.BaseFragment
import de.bitb.pantryplaner.ui.base.BaseViewModel
import de.bitb.pantryplaner.ui.base.NaviEvent

data class ReleaseVersion(val version: String, val commits: List<String>)

//@HiltViewModel // just for interface
class ReleaseNotesViewModel : BaseViewModel() {

    lateinit var notes: List<ReleaseVersion>

    fun loadPatchNotes(rawNotes: String) {
        //TODO load via usecase at the beginning
        notes = Gson().fromJson(rawNotes, Array<ReleaseVersion>::class.java).toList()
    }

}

@AndroidEntryPoint
class ReleaseNotesFragment : BaseFragment<ReleaseNotesViewModel>() {
    companion object {
        val naviFromLogin: NaviEvent = NaviEvent.Navigate(R.id.login_to_releasenotes)
        val naviFromSetting: NaviEvent = NaviEvent.Navigate(R.id.settings_to_releasenotes)

        const val APPBAR_TAG = "ReleaseNotesAppbar"
        const val LIST_TAG = "ReleaseNotesList" //TODO make TestTag
    }

    override val viewModel: ReleaseNotesViewModel = ReleaseNotesViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadPatchNotes(readTextFromAsset(requireContext(), "releaseNotes.json"))
    }

    @Composable
    override fun screenContent() {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text("Release Notes") },
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag(LIST_TAG),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                contentPadding = PaddingValues(4.dp),
            ) {
                val notes = viewModel.notes
                items(notes.size) { VersionItem(notes[it]) }
            }
        }
    }

    @Composable
    private fun VersionItem(releaseVersion: ReleaseVersion) {
        Column(
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                text = releaseVersion.version.trim(),
                textDecoration = TextDecoration.Underline
            )
            releaseVersion.commits
                .map { "- ${it.trim()}" }
                .forEach {
                    Text(
                        modifier = Modifier.padding(start = 20.dp, top = 8.dp),
                        text = it
                    )
                }
        }
    }

}