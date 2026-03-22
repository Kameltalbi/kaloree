package com.kaloree.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaloree.ui.theme.PrimaryGreen
import com.kaloree.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val bmr by viewModel.bmr.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()

    var editAge by remember { mutableStateOf("") }
    var editWeight by remember { mutableStateOf("") }
    var editHeight by remember { mutableStateOf("") }
    var editGoal by remember { mutableStateOf("") }

    if (isEditing) {
        user?.let { currentUser ->
            LaunchedEffect(currentUser) {
                editAge = currentUser.age.toString()
                editWeight = currentUser.weight.toString()
                editHeight = currentUser.height.toString()
                editGoal = currentUser.goal
            }
        }

        AlertDialog(
            onDismissRequest = viewModel::hideEditDialog,
            title = { Text("Modifier le profil") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editAge,
                        onValueChange = { editAge = it.filter { c -> c.isDigit() } },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editWeight,
                        onValueChange = { editWeight = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Poids (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editHeight,
                        onValueChange = { editHeight = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Taille (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Objectif:")
                    val goals = listOf("perte" to "Perte de poids", "maintien" to "Maintien", "prise" to "Prise de masse")
                    goals.forEach { (value, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = editGoal == value,
                                onClick = { editGoal = value },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryGreen)
                            )
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateUser(
                            age = editAge.toIntOrNull(),
                            weight = editWeight.toDoubleOrNull(),
                            height = editHeight.toDoubleOrNull(),
                            goal = editGoal
                        )
                        viewModel.hideEditDialog()
                    }
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideEditDialog) {
                    Text("Annuler")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil") },
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
            user?.let { currentUser ->
                // User Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryGreen.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Informations personnelles",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileInfoRow("Sexe", if (currentUser.gender == "M") "Homme" else "Femme")
                        ProfileInfoRow("Age", "${currentUser.age} ans")
                        ProfileInfoRow("Poids", "${String.format("%.1f", currentUser.weight)} kg")
                        ProfileInfoRow("Taille", "${String.format("%.1f", currentUser.height)} cm")
                        ProfileInfoRow(
                            "Objectif", 
                            when (currentUser.goal) {
                                "perte" -> "Perte de poids"
                                "maintien" -> "Maintien"
                                "prise" -> "Prise de masse"
                                else -> currentUser.goal
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Calorie Info Card
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Calcul calorique",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileInfoRow("BMR (Metabolisme basal)", "${bmr.toInt()} kcal")
                        ProfileInfoRow(
                            "Objectif quotidien", 
                            "${currentUser.calorieTarget} kcal",
                            valueColor = PrimaryGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Edit Button
                Button(
                    onClick = viewModel::showEditDialog,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("Modifier le profil")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Delete Data Button
                OutlinedButton(
                    onClick = viewModel::deleteAllData,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reinitialiser toutes les donnees")
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aucun profil trouve")
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(
    label: String, 
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}
