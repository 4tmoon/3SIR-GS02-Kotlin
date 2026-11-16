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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.atmoon.zeloapp.data.model.Issue
import com.github.atmoon.zeloapp.ui.theme.ZeloDanger
import com.github.atmoon.zeloapp.ui.theme.ZeloPrimary
import com.github.atmoon.zeloapp.ui.theme.ZeloPrimarySoft
import com.github.atmoon.zeloapp.ui.theme.ZeloSuccess
import com.github.atmoon.zeloapp.ui.theme.ZeloSuccessSoft
import com.github.atmoon.zeloapp.ui.theme.ZeloTextMuted
import com.github.atmoon.zeloapp.ui.viewmodel.ZeloViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.find
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueDetailScreen(
    issueId: String,
    viewModel: ZeloViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val issue = state.allIssues.find { it.id == issueId }
    val comments by viewModel.observeComments(issueId).collectAsStateWithLifecycle(initialValue = emptyList())

    var commentText by remember { mutableStateOf("") }
    var showNoticeDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.feedbackMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da ocorrência", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (issue != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "Excluir",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ZeloPrimary,
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (issue == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Ocorrência não encontrada.", color = ZeloTextMuted)
            }
            return@Scaffold
        }

        val percent = (issue.progress * 100).roundToInt()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                StatusChip(issue.category, ZeloPrimary, ZeloPrimarySoft)
                SeverityChip(issue.severity)
                StatusChip(
                    text = issue.status,
                    textColor = if (issue.hasNotice) ZeloSuccess else ZeloTextMuted,
                    bgColor = if (issue.hasNotice) ZeloSuccessSoft else Color(0xFFE3E5EF),
                    icon = if (issue.hasNotice) Icons.Default.Gavel else null
                )
            }

            Text(issue.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = ZeloTextMuted, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(issue.address, color = ZeloTextMuted, fontSize = 14.sp)
            }

            Text(issue.description, fontSize = 15.sp)

            // Card de Apoio Comunitário
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE3E5EF))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Apoio da comunidade", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { issue.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (issue.reachedThreshold) ZeloSuccess else ZeloPrimary,
                        trackColor = ZeloPrimarySoft
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "${issue.likes} de ${issue.activeRegionalUsers} moradores apoiaram ($percent%) • Meta: ${issue.goalSupports} apoios (80%)",
                        fontSize = 12.sp,
                        color = ZeloTextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (issue.supportedByMe) {
                        OutlinedButton(
                            onClick = { viewModel.toggleSupport(issue) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.ThumbUp, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Apoio registrado (desfazer)")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.toggleSupport(issue) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Outlined.ThumbUp, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Apoiar esta ocorrência")
                        }
                    }

                    if (issue.hasNotice) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { showNoticeDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Visualizar Ofício Formal Nº ${issue.noticeNumber}")
                        }
                    }
                }
            }

            // Linha do tempo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE3E5EF))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Histórico da solicitação", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    TimelineStepRow(
                        done = true,
                        title = "Ocorrência registrada",
                        subtitle = formatDate(issue.createdAt)
                    )
                    TimelineStepRow(
                        done = issue.reachedAt != null,
                        title = "Meta de 80% de apoio atingida",
                        subtitle = if (issue.reachedAt != null) formatDate(issue.reachedAt)
                        else "Faltam ${issue.supportsMissing} apoios para a meta"
                    )
                    TimelineStepRow(
                        done = issue.hasNotice,
                        title = "Ofício formal gerado",
                        subtitle = if (issue.noticeGeneratedAt != null) "Ofício Nº ${issue.noticeNumber} emitido"
                        else "Gerado automaticamente ao atingir 80%"
                    )
                    TimelineStepRow(
                        done = issue.acceptedByCity,
                        title = "Resposta da prefeitura",
                        subtitle = when {
                            issue.acceptedByCity -> "Solicitação aceita pela prefeitura"
                            issue.hasNotice -> "Aguardando análise do órgão responsável"
                            else -> "Disponível após emissão do ofício"
                        }
                    )
                }
            }

            // Comentários
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE3E5EF))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Comentários (${comments.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("Escreva um comentário...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(onClick = {
                            if (commentText.isNotBlank()) {
                                viewModel.addComment(issue.id, commentText)
                                commentText = ""
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = ZeloPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (comments.isEmpty()) {
                        Text("Seja o primeiro a comentar.", color = ZeloTextMuted, fontSize = 13.sp)
                    } else {
                        comments.forEach { comment ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Text(comment.author, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(comment.text, fontSize = 13.sp)
                            }
                            HorizontalDivider(color = Color(0xFFF0F0F5))
                        }
                    }
                }
            }
        }

        if (showNoticeDialog) {
            OfficialNoticeDialog(issue = issue, onDismiss = { showNoticeDialog = false })
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Excluir ocorrência") },
                text = { Text("Deseja remover permanentemente esta ocorrência do banco de dados local?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        viewModel.deleteIssue(issue, onDeleted = onNavigateBack)
                    }) {
                        Text("Excluir", color = ZeloDanger)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
private fun TimelineStepRow(done: Boolean, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (done) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (done) ZeloSuccess else ZeloTextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, color = ZeloTextMuted, fontSize = 12.sp)
        }
    }
}

@Composable
fun OfficialNoticeDialog(issue: Issue, onDismiss: () -> Unit) {
    val percent = (issue.progress * 100).roundToInt()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("OFÍCIO Nº ${issue.noticeNumber}", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ZELO — ZELADORIA URBANA COLABORATIVA", fontWeight = FontWeight.Bold, color = ZeloPrimary, fontSize = 12.sp)
                HorizontalDivider()
                Text("Destinatário: Secretaria Municipal de Zeladoria Urbana", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Assunto: Solicitação de providências — ${issue.title}", fontSize = 13.sp)
                Text("Local: ${issue.address}", fontSize = 13.sp)
                Text(
                    "A comunidade local atingiu $percent% de adesão (${issue.likes} de ${issue.activeRegionalUsers} moradores ativos), superando a meta mínima de 80% exigida para formalização.",
                    fontSize = 13.sp
                )
                Text("Descrição: ${issue.description}", fontSize = 12.sp, color = ZeloTextMuted)
                HorizontalDivider()
                Text(
                    "Código de autenticação: ${issue.verificationCode}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = ZeloSuccess
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Fechar") }
        }
    )
}

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR"))
    return sdf.format(Date(millis))
}