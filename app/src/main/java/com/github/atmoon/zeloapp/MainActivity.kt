package com.github.atmoon.zeloapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zelo.app.data.local.ZeloDatabase
import com.zelo.app.data.repository.IssueRepository
import com.zelo.app.ui.navigation.ZeloNavGraph
import com.zelo.app.ui.theme.ZeloTheme
import com.zelo.app.ui.viewmodel.ZeloViewModel

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