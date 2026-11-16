package com.github.atmoon.zeloapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.atmoon.zeloapp.data.model.Issue
import com.github.atmoon.zeloapp.data.model.IssueCategory
import com.github.atmoon.zeloapp.data.model.IssueSeverity
import com.github.atmoon.zeloapp.ui.theme.ZeloPrimary
import com.github.atmoon.zeloapp.ui.theme.ZeloPrimarySoft
import com.github.atmoon.zeloapp.ui.theme.ZeloSuccess
import com.github.atmoon.zeloapp.ui.theme.ZeloTextMuted
import com.github.atmoon.zeloapp.ui.theme.ZeloWarning

@Composable
fun StatsScreen(issues: List<Issue>) {
    val total = issues.size
    val totalSupports = issues.sumOf { it.likes }
    val withNotice = issues.count { it.hasNotice }
    val accepted = issues.count { it.acceptedByCity }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            KpiCard(Icons.Default.Gavel, "Ocorrências", "$total", ZeloPrimary, Modifier.weight(1f))
            KpiCard(Icons.Default.ThumbUp, "Apoios", "$totalSupports", ZeloPrimary, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            KpiCard(Icons.Default.Description, "Ofícios gerados", "$withNotice", ZeloWarning, Modifier.weight(1f))
            KpiCard(Icons.Default.Verified, "Aceitas", "$accepted", ZeloSuccess, Modifier.weight(1f))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE3E5EF))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Distribuição por Categoria", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                IssueCategory.all.forEach { cat ->
                    val count = issues.count { it.category == cat }
                    StatBarRow(label = cat, value = count, max = total, color = ZeloPrimary)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE3E5EF))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Distribuição por Gravidade", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                IssueSeverity.all.forEach { sev ->
                    val count = issues.count { it.severity == sev }
                    StatBarRow(label = IssueSeverity.label(sev), value = count, max = total, color = ZeloWarning)
                }
            }
        }
    }
}

@Composable
private fun KpiCard(icon: ImageVector, label: String, value: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE3E5EF))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, fontSize = 12.sp, color = ZeloTextMuted)
        }
    }
}

@Composable
private fun StatBarRow(label: String, value: Int, max: Int, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("$value", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { if (max == 0) 0f else value.toFloat() / max.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = ZeloPrimarySoft
        )
    }
}