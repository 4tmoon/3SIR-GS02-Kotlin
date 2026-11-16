package com.github.atmoon.zeloapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.atmoon.zeloapp.data.model.IssueCategory
import com.github.atmoon.zeloapp.data.model.IssueSeverity
import com.github.atmoon.zeloapp.ui.theme.ZeloPrimary
import com.github.atmoon.zeloapp.ui.viewmodel.ZeloViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateIssueScreen(
    viewModel: ZeloViewModel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(IssueCategory.ACCESSIBILITY) }
    var severity by remember { mutableStateOf(IssueSeverity.MEDIUM) }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var attemptedSubmit by remember { mutableStateOf(false) }

    val titleError = attemptedSubmit && title.trim().length < 5
    val addressError = attemptedSubmit && address.trim().isEmpty()
    val descriptionError = attemptedSubmit && description.trim().length < 10

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova ocorrência", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ZeloPrimary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título da ocorrência *") },
                placeholder = { Text("Ex.: Calçada sem rampa de acesso") },
                isError = titleError,
                supportingText = {
                    if (titleError) Text("Informe um título com pelo menos 5 caracteres.")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    IssueCategory.all.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                category = item
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Column {
                Text("Gravidade", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IssueSeverity.all.forEach { sev ->
                        FilterChip(
                            selected = severity == sev,
                            onClick = { severity = sev },
                            label = { Text(IssueSeverity.label(sev)) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Endereço completo *") },
                placeholder = { Text("Ex.: Av. Lins de Vasconcelos, 1222 - SP") },
                isError = addressError,
                supportingText = {
                    if (addressError) Text("O endereço é obrigatório.")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição detalhada *") },
                placeholder = { Text("Descreva o problema e como ele afeta os moradores") },
                isError = descriptionError,
                supportingText = {
                    if (descriptionError) Text("Descreva o problema com pelo menos 10 caracteres.")
                },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    attemptedSubmit = true
                    val isValid = title.trim().length >= 5 &&
                            address.trim().isNotEmpty() &&
                            description.trim().length >= 10
                    if (isValid) {
                        viewModel.createIssue(
                            title = title,
                            description = description,
                            category = category,
                            address = address,
                            severity = severity,
                            onCreated = onNavigateBack
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.padding(4.dp))
                Text("Publicar ocorrência", fontWeight = FontWeight.Bold)
            }
        }
    }
}