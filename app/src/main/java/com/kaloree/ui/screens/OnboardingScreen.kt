package com.kaloree.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
nimport androidx.compose.foundation.selection.selectableGroup
nimport androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaloree.ui.theme.PrimaryGreen
import com.kaloree.viewmodel.OnboardingViewModel

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val onboardingComplete by viewModel.onboardingComplete.collectAsStateWithLifecycle()
    val existingUser by viewModel.existingUser.collectAsStateWithLifecycle()

    LaunchedEffect(onboardingComplete) {
        if (onboardingComplete) {
            onOnboardingComplete()
        }
    }

    LaunchedEffect(existingUser) {
        if (existingUser != null) {
            onOnboardingComplete()
        }
    }

    var gender by remember { mutableStateOf("M") }
    var age by remember { mutableStateOf("25") }
    var weight by remember { mutableStateOf("70") }
    var height by remember { mutableStateOf("175") }
    var goal by remember { mutableStateOf("maintien") }

    var showPreview by remember { mutableStateOf(false) }
    val previewCalories = remember(gender, age, weight, height, goal) {
        viewModel.calculatePreviewCalories(
            gender,
            age.toIntOrNull() ?: 25,
            weight.toDoubleOrNull() ?? 70.0,
            height.toDoubleOrNull() ?? 175.0,
            goal
        )
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Bienvenue sur Kaloree",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Configure ton profil pour commencer",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Gender Selection
            Text(
                text = "Sexe",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GenderOption(
                    text = "Homme",
                    selected = gender == "M",
                    onClick = { gender = "M" },
                    modifier = Modifier.weight(1f)
                )
                GenderOption(
                    text = "Femme",
                    selected = gender == "F",
                    onClick = { gender = "F" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Age
            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter { c -> c.isDigit() } },
                label = { Text("Âge (années)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weight
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Poids actuel (kg)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Height
            OutlinedTextField(
                value = height,
                onValueChange = { height = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Taille (cm)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Goal
            Text(
                text = "Objectif",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup()
            ) {
                GoalOption(
                    text = "Perte de poids (-500 kcal)",
                    selected = goal == "perte",
                    onClick = { goal = "perte" }
                )
                GoalOption(
                    text = "Maintien",
                    selected = goal == "maintien",
                    onClick = { goal = "maintien" }
                )
                GoalOption(
                    text = "Prise de masse (+300 kcal)",
                    selected = goal == "prise",
                    onClick = { goal = "prise" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Calorie Preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryGreen.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Objectif calorique quotidien",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$previewCalories kcal",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.saveUser(
                        gender = gender,
                        age = age.toIntOrNull() ?: 25,
                        weight = weight.toDoubleOrNull() ?: 70.0,
                        height = height.toDoubleOrNull() ?: 175.0,
                        goal = goal
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                )
            ) {
                Text("Commencer")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun GenderOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) PrimaryGreen.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (selected) {
            CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
                brush = SolidColor(PrimaryGreen)
            )
        } else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) PrimaryGreen else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun GoalOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = PrimaryGreen
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
