package com.github.atmoon.zeloapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.atmoon.zeloapp.data.model.Issue
import com.github.atmoon.zeloapp.data.model.IssueCategory
import com.github.atmoon.zeloapp.data.model.IssueSeverity
import com.github.atmoon.zeloapp.data.model.IssueSort
import com.github.atmoon.zeloapp.ui.theme.ZeloDanger
import com.github.atmoon.zeloapp.ui.theme.ZeloDangerSoft
import com.github.atmoon.zeloapp.ui.theme.ZeloPrimary
import com.github.atmoon.zeloapp.ui.theme.ZeloPrimarySoft
import com.github.atmoon.zeloapp.ui.theme.ZeloSuccess
import com.github.atmoon.zeloapp.ui.theme.ZeloSuccessSoft
import com.github.atmoon.zeloapp.ui.theme.ZeloTextMuted
import com.github.atmoon.zeloapp.ui.theme.ZeloWarning
import com.github.atmoon.zeloapp.ui.theme.ZeloWarningSoft
import com.github.atmoon.zeloapp.ui.viewmodel.ZeloUiState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FeedScreen(
    state: ZeloUiState,
    onSearchChange: (String) -> Unit,
    onToggleCategory: (String) -> Unit,
    onToggleSeverity: (String) -> Unit,
    onToggleSupportedOnly: (Boolean) -> Unit,
    onToggleNoticeOnly: (Boolean) -> Unit,
    onClearFilters: () -> Unit,
    onSelectSort: (IssueSort) -> Unit,
    onOpenDetail: (String) -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("Buscar título ou endereço", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpar")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            Box {
                OutlinedButton(
                    onClick = { showSortMenu = true },
                    contentPadding = PaddingValues(12.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Sort, contentDescription = "Ordenar")
                }
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    IssueSort.entries.forEach { sort ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    sort.label,
                                    fontWeight = if (state.sortOrder == sort) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSelectSort(sort)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }

            BadgedBox(
                badge = {
                    if (state.activeFilterCount > 0) {
                        Badge { Text("${state.activeFilterCount}") }
                    }
                }
            ) {
                OutlinedButton(
                    onClick = { showFilterSheet = true },
                    contentPadding = PaddingValues(12.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filtros")
                }
            }
        }

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.filteredIssues.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = ZeloTextMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Nenhuma ocorrência encontrada.", color = ZeloTextMuted)
                        if (state.activeFilterCount > 0 || state.searchQuery.isNotEmpty()) {
                            TextButton(onClick = {
                                onClearFilters()
                                onSearchChange("")
                            }) {
                                Text("Limpar busca e filtros")
                            }
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredIssues, key = { it.id }) { issue ->
                        IssueItemCard(issue = issue, onClick = { onOpenDetail(issue.id) })
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filtros", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = onClearFilters) { Text("Limpar") }
                }

                Text("Categoria", fontSize = 13.sp, color = ZeloTextMuted)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IssueCategory.all.forEach { cat ->
                        FilterChip(
                            selected = cat in state.selectedCategories,
                            onClick = { onToggleCategory(cat) },
                            label = { Text(cat) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Gravidade", fontSize = 13.sp, color = ZeloTextMuted)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IssueSeverity.all.forEach { sev ->
                        FilterChip(
                            selected = sev in state.selectedSeverities,
                            onClick = { onToggleSeverity(sev) },
                            label = { Text(IssueSeverity.label(sev)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Status e participação", fontSize = 13.sp, color = ZeloTextMuted)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = state.onlySupported,
                        onClick = { onToggleSupportedOnly(!state.onlySupported) },
                        label = { Text("Apoiadas por mim") }
                    )
                    FilterChip(
                        selected = state.onlyWithNotice,
                        onClick = { onToggleNoticeOnly(!state.onlyWithNotice) },
                        label = { Text("Com ofício formal") }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aplicar filtros")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun IssueItemCard(issue: Issue, onClick: () -> Unit) {
    val percent = (issue.progress * 100).roundToInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE3E5EF))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusChip(
                        text = issue.category,
                        textColor = ZeloPrimary,
                        bgColor = ZeloPrimarySoft
                    )
                    SeverityChip(severity = issue.severity)
                }
                if (issue.hasNotice) {
                    StatusChip(
                        text = "Oficial",
                        textColor = ZeloSuccess,
                        bgColor = ZeloSuccessSoft,
                        icon = Icons.Default.Gavel
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = issue.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = ZeloTextMuted
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = issue.address,
                    fontSize = 12.sp,
                    color = ZeloTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { issue.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (issue.reachedThreshold) ZeloSuccess else ZeloPrimary,
                trackColor = ZeloPrimarySoft
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${issue.likes} de ${issue.activeRegionalUsers} apoios ($percent%) • Meta: 80%",
                fontSize = 12.sp,
                color = ZeloTextMuted
            )
        }
    }
}

@Composable
fun StatusChip(
    text: String,
    textColor: Color,
    bgColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Row(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(text = text, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SeverityChip(severity: String) {
    val (color, soft) = when (severity) {
        IssueSeverity.HIGH -> ZeloDanger to ZeloDangerSoft
        IssueSeverity.LOW -> ZeloSuccess to ZeloSuccessSoft
        else -> ZeloWarning to ZeloWarningSoft
    }
    StatusChip(
        text = "Gravidade ${IssueSeverity.label(severity).lowercase()}",
        textColor = color,
        bgColor = soft
    )
}