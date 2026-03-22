package com.kaloree.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaloree.ui.theme.PrimaryGreen
import com.kaloree.viewmodel.AddActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(
    onNavigateBack: () -> Unit,
    onActivityAdded: () -> Unit,
    viewModel: AddActivityViewModel = viewModel()
) {
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val duration by viewModel.duration.collectAsStateWithLifecycle()
    val distance by viewModel.distance.collectAsStateWithLifecycle()
    val manualCalories by viewModel.manualCalories.collectAsStateWithLifecycle()
    val useManualCalories by viewModel.useManualCalories.collectAsStateWithLifecycle()
    val calculatedCalories by viewModel.calculatedCalories.collectAsStateWithLifecycle()
    val activityTypes = viewModel.activityTypes

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajouter une activite") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Activity Type Selection
            Text(
                text = "Type d'activite",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                activityTypes.forEach { type ->
                    val displayName = when (type.lowercase()) {
                        "walking" -> "Marche"
                        "running" -> "Course"
                        "swimming" -> "Natation"
                        "weightlifting" -> "Musculation"
                        "cycling" -> "Velo"
                        else -> type.replaceFirstChar { it.uppercase() }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedType == type,
                                onClick = { viewModel.setType(type) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == type,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PrimaryGreen
                            )
                        )
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Duration
            OutlinedTextField(
                value = duration.toString(),
                onValueChange = {
                    val newDuration = it.toIntOrNull() ?: 0
                    viewModel.setDuration(newDuration)
                },
                label = { Text("Duree (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { Text("min") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Distance (optional)
            OutlinedTextField(
                value = distance?.toString() ?: "",
                onValueChange = {
                    val newDistance = it.toDoubleOrNull()
                    viewModel.setDistance(newDistance)
                },
                label = { Text("Distance (optionnel)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text("km") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Manual Calories Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calories manuelles",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = useManualCalories,
                    onCheckedChange = viewModel::setUseManualCalories,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PrimaryGreen
                    )
                )
            }

            if (useManualCalories) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = manualCalories?.toString() ?: "",
                    onValueChange = {
                        val newCalories = it.toDoubleOrNull()
                        viewModel.setManualCalories(newCalories)
                    },
                    label = { Text("Calories brulees") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("kcal") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Calorie Preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF6B6B).copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calories brulees",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${calculatedCalories.toInt()} kcal",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Add Button
            Button(
                onClick = {
                    onActivityAdded()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                ),
                enabled = duration > 0 && (!useManualCalories || manualCalories != null)
            ) {
                Text("Ajouter l'activite")
            }
        }
    }
}

