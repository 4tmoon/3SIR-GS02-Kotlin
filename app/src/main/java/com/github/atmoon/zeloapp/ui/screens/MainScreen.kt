package com.github.atmoon.zeloapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.atmoon.zeloapp.ui.theme.ZeloPrimary
import com.github.atmoon.zeloapp.ui.viewmodel.ZeloViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: ZeloViewModel,
    onOpenCreate: () -> Unit,
    onOpenDetail: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.feedbackMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val titles = listOf("Zelo — Ocorrências", "Ofícios Públicos", "Estatísticas")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titles[selectedTab], fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ZeloPrimary,
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = onOpenCreate,
                    containerColor = ZeloPrimary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Reportar") }
                )
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = "Feed") },
                    label = { Text("Feed") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Gavel, contentDescription = "Ofícios") },
                    label = { Text("Ofícios") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Insights, contentDescription = "Estatísticas") },
                    label = { Text("Estatísticas") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> FeedScreen(
                    state = state,
                    onSearchChange = viewModel::updateSearchQuery,
                    onToggleCategory = viewModel::toggleCategoryFilter,
                    onToggleSeverity = viewModel::toggleSeverityFilter,
                    onToggleSupportedOnly = viewModel::setOnlySupported,
                    onToggleNoticeOnly = viewModel::setOnlyWithNotice,
                    onClearFilters = viewModel::clearFilters,
                    onSelectSort = viewModel::updateSortOrder,
                    onOpenDetail = onOpenDetail
                )
                1 -> NoticesScreen(
                    issues = state.allIssues.filter { it.hasNotice },
                    onOpenDetail = onOpenDetail
                )
                2 -> StatsScreen(issues = state.allIssues)
            }
        }
    }
}
