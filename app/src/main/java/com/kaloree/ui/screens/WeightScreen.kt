package com.kaloree.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaloree.data.entity.WeightLog
import com.kaloree.ui.theme.PrimaryGreen
import com.kaloree.viewmodel.WeightViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(
    onNavigateBack: () -> Unit,
    viewModel: WeightViewModel = viewModel()
) {
    val weightLogs by viewModel.weightLogs.collectAsStateWithLifecycle()
    val latestWeight by viewModel.latestWeight.collectAsStateWithLifecycle()
    val weightChange by viewModel.weightChange.collectAsStateWithLifecycle()
    val isAddingWeight by viewModel.isAddingWeight.collectAsStateWithLifecycle()

    var newWeight by remember { mutableStateOf("") }

    if (isAddingWeight) {
        AlertDialog(
            onDismissRequest = viewModel::hideAddWeightDialog,
            title = { Text("Ajouter un poids") },
            text = {
                OutlinedTextField(
                    value = newWeight,
                    onValueChange = { newWeight = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Poids (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("kg") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        newWeight.toDoubleOrNull()?.let {
                            viewModel.addWeightLog(it)
                            newWeight = ""
                        }
                    },
                    enabled = newWeight.toDoubleOrNull() != null
                ) {
                    Text("Ajouter")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideAddWeightDialog) {
                    Text("Annuler")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suivi du poids") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::showAddWeightDialog,
                containerColor = PrimaryGreen
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter poids")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Current Weight Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryGreen.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Poids actuel",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = latestWeight?.let { String.format("%.1f", it) } ?: "--",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                    Text(
                        text = "kg",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    weightChange?.let { change ->
                        Spacer(modifier = Modifier.height(8.dp))
                        val changeText = if (change > 0) "+${String.format("%.1f", change)}" 
                                        else String.format("%.1f", change)
                        val changeColor = if (change < 0) PrimaryGreen 
                                         else androidx.compose.ui.graphics.Color(0xFFFF6B6B)
                        Text(
                            text = "$changeText kg depuis le debut",
                            style = MaterialTheme.typography.bodyMedium,
                            color = changeColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weight History
            Text(
                text = "Historique (${weightLogs.size} entrees)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (weightLogs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun poids enregistre\nAppuie sur + pour ajouter",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(weightLogs) { log ->
                        WeightLogItem(
                            log = log,
                            onDelete = { viewModel.deleteWeightLog(log) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeightLogItem(
    log: WeightLog,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${String.format("%.1f", log.weight)} kg",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateFormat.format(Date(log.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
