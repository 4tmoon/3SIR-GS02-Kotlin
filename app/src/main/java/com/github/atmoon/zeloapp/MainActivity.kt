package com.github.atmoon.zeloapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.atmoon.zeloapp.data.local.ZeloDatabase
import com.github.atmoon.zeloapp.data.repository.IssueRepository
import com.github.atmoon.zeloapp.ui.navigation.ZeloNavGraph
import com.github.atmoon.zeloapp.ui.theme.ZeloTheme
import com.github.atmoon.zeloapp.ui.viewmodel.ZeloViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = ZeloDatabase.getInstance(applicationContext)
        val repository = IssueRepository(database.issueDao())

        setContent {
            ZeloTheme {
                val zaloViewModel: ZeloViewModel = viewModel(
                    factory = ZeloViewModel.Factory(repository)
                )
                ZeloNavGraph(viewModel = zaloViewModel)
            }
        }
    }
}