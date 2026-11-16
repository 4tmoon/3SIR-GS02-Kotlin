package com.github.atmoon.zeloapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.atmoon.zeloapp.data.model.Issue
import com.github.atmoon.zeloapp.ui.theme.ZeloSuccess
import com.github.atmoon.zeloapp.ui.theme.ZeloSuccessSoft
import com.github.atmoon.zeloapp.ui.theme.ZeloTextMuted
import kotlin.math.roundToInt

@Composable
fun NoticesScreen(
    issues: List<Issue>,
    onOpenDetail: (String) -> Unit
) {
    var previewIssue by remember { mutableStateOf<Issue?>(null) }

    if (issues.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Gavel,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = ZeloTextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Nenhum ofício formal gerado ainda.", color = ZeloTextMuted)
            }
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(issues, key = { it.id }) { notice ->
            val percent = (notice.progress * 100).roundToInt()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenDetail(notice.id) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZeloSuccessSoft),
                border = BorderStroke(1.dp, ZeloSuccess.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = ZeloSuccess)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ofício Nº ${notice.noticeNumber}", fontSize = 11.sp, color = ZeloTextMuted)
                            Text(notice.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${notice.address} • $percent% de apoio", fontSize = 12.sp, color = ZeloTextMuted)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { previewIssue = notice },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Visualizar documento")
                    }
                }
            }
        }
    }

    previewIssue?.let { issue ->
        OfficialNoticeDialog(issue = issue, onDismiss = { previewIssue = null })
    }
}