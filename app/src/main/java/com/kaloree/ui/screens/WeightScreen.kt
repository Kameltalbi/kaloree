package com.kaloree.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
    viewModel: WeightViewModel = viewModel()
) {
    val weightLogs by viewModel.weightLogs.collectAsStateWithLifecycle()
    val latestWeight by viewModel.latestWeight.collectAsStateWithLifecycle()
    val progress by viewModel.weightProgress.collectAsStateWithLifecycle()
    val isAddingWeight by viewModel.isAddingWeight.collectAsStateWithLifecycle()
    val isEditingGoal by viewModel.isEditingGoal.collectAsStateWithLifecycle()
    val user by viewModel.user.collectAsStateWithLifecycle()

    var newWeight by remember { mutableStateOf("") }
    var newTargetWeight by remember { mutableStateOf("") }
    var newDuration by remember { mutableStateOf("") }

    // Add weight dialog
    if (isAddingWeight) {
        AlertDialog(
            onDismissRequest = viewModel::hideAddWeightDialog,
            title = { Text("Enregistrer mon poids") },
            text = {
                OutlinedTextField(
                    value = newWeight,
                    onValueChange = { newWeight = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Poids actuel (kg)") },
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
                ) { Text("Enregistrer") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideAddWeightDialog) { Text("Annuler") }
            }
        )
    }

    // Edit goal dialog
    if (isEditingGoal) {
        LaunchedEffect(isEditingGoal) {
            newTargetWeight = user?.targetWeight?.let { String.format("%.1f", it) } ?: ""
            newDuration = user?.durationMonths?.toString() ?: "6"
        }
        AlertDialog(
            onDismissRequest = viewModel::hideEditGoalDialog,
            title = { Text("Modifier l’objectif") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newTargetWeight,
                        onValueChange = { newTargetWeight = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Poids cible (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        suffix = { Text("kg") }
                    )
                    OutlinedTextField(
                        value = newDuration,
                        onValueChange = { newDuration = it.filter { c -> c.isDigit() } },
                        label = { Text("Durée (mois)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        suffix = { Text("mois") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val tw = newTargetWeight.toDoubleOrNull()
                        val dm = newDuration.toIntOrNull()
                        if (tw != null && dm != null && dm > 0) {
                            viewModel.updateGoal(tw, dm)
                        }
                    },
                    enabled = newTargetWeight.toDoubleOrNull() != null &&
                              (newDuration.toIntOrNull() ?: 0) > 0
                ) { Text("Mettre à jour") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideEditGoalDialog) { Text("Annuler") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suivi du poids") },
                actions = {
                    if (user?.targetWeight != null) {
                        IconButton(onClick = viewModel::showEditGoalDialog) {
                            Icon(Icons.Default.Edit, contentDescription = "Modifier objectif")
                        }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Progress card (only shown if target weight set)
            progress?.let { p ->
                item {
                    WeightProgressCard(p)
                }
            } ?: item {
                // Simple current weight card when no target
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Poids actuel", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = latestWeight?.let { String.format("%.1f kg", it) } ?: "-- kg",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = viewModel::showEditGoalDialog) {
                            Text("+ Définir un objectif de poids")
                        }
                    }
                }
            }

            // History header
            item {
                Text(
                    text = "Historique (${weightLogs.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (weightLogs.isEmpty()) {
                item {
                    Text(
                        text = "Aucune entrée\nAppuie sur + pour enregistrer ton poids",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                }
            } else {
                items(weightLogs) { log ->
                    WeightLogItem(log = log, onDelete = { viewModel.deleteWeightLog(log) })
                }
            }
        }
    }
}

@Composable
fun WeightProgressCard(p: com.kaloree.viewmodel.WeightProgress) {
    val isLosing = p.targetWeight < p.startWeight
    val directionColor = if (isLosing) PrimaryGreen else Color(0xFF2196F3)
    val trendColor = when {
        p.weeklyTrend == null -> MaterialTheme.colorScheme.onSurfaceVariant
        isLosing && p.weeklyTrend < 0 -> PrimaryGreen
        !isLosing && p.weeklyTrend > 0 -> Color(0xFF2196F3)
        else -> Color(0xFFFF6B6B)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = directionColor.copy(alpha = 0.07f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row: current vs target
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Poids actuel", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(String.format("%.1f kg", p.currentWeight),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold, color = directionColor)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (isLosing) "🏆 Objectif" else "🏆 Objectif",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(String.format("%.1f kg", p.targetWeight),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (isLosing) "À perdre" else "À prendre",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(String.format("%.1f kg", kotlin.math.abs(p.remainingKg)),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Progress bar
            Text("Progression : ${p.progressPercent.toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { p.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = directionColor,
                trackColor = directionColor.copy(alpha = 0.2f)
            )

            Spacer(Modifier.height(12.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Weekly trend
                Column {
                    Text("Tendance / semaine",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val trendText = p.weeklyTrend?.let {
                        val sign = if (it >= 0) "+" else ""
                        "$sign${String.format("%.2f", it)} kg"
                    } ?: "-- kg"
                    Text(trendText, style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium, color = trendColor)
                }
                // Estimated time
                Column(horizontalAlignment = Alignment.End) {
                    Text("Date estimée",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val estText = p.estimatedDaysLeft?.let {
                        when {
                            it < 30 -> "$it jours"
                            it < 365 -> "${it / 30} mois"
                            else -> "${it / 365} an(s)"
                        }
                    } ?: "N/A"
                    Text(estText, style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium)
                }
            }

            // On-track indicator
            if (p.weeklyTrend != null) {
                Spacer(Modifier.height(8.dp))
                val (statusText, statusColor) = if (p.isOnTrack)
                    "✅ Sur la bonne voie" to PrimaryGreen
                else
                    "⚠️ Rythme insuffisant — augmente ton déficit ou ton activité" to Color(0xFFFF6B6B)
                Text(statusText, style = MaterialTheme.typography.bodySmall, color = statusColor)
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
