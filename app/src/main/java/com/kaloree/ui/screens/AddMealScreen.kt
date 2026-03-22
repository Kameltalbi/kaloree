package com.kaloree.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
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
import com.kaloree.data.entity.Food
import com.kaloree.ui.theme.PrimaryGreen
import com.kaloree.viewmodel.AddMealViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    onNavigateBack: () -> Unit,
    onMealAdded: () -> Unit,
    viewModel: AddMealViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val foods by viewModel.foods.collectAsStateWithLifecycle()
    val selectedFood by viewModel.selectedFood.collectAsStateWithLifecycle()
    val usePortions by viewModel.usePortions.collectAsStateWithLifecycle()
    val portionCount by viewModel.portionCount.collectAsStateWithLifecycle()
    val grams by viewModel.grams.collectAsStateWithLifecycle()
    val effectiveGrams by viewModel.effectiveGrams.collectAsStateWithLifecycle()
    val calculatedCalories by viewModel.calculatedCalories.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajouter un repas") },
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
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::setSearchQuery,
                label = { Text("Rechercher un aliment") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Food List or Selected Food Details
            if (selectedFood == null) {
                // Show food list
                Text(
                    text = "Selectionne un aliment",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(foods) { food ->
                        FoodListItem(
                            food = food,
                            onClick = { viewModel.selectFood(food) }
                        )
                    }
                }
            } else {
                // Show selected food details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryGreen.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedFood!!.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = { viewModel.clearSelection() }) {
                                Text("Changer")
                            }
                        }

                        Text(
                            text = "${selectedFood!!.caloriesPer100g.toInt()} kcal / 100g",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Portions / Grammes toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = usePortions,
                        onClick = { viewModel.setUsePortions(true) },
                        label = { Text("${selectedFood!!.portionUnit.replaceFirstChar { it.uppercase() }}s") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !usePortions,
                        onClick = { viewModel.setUsePortions(false) },
                        label = { Text("Grammes") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (usePortions) {
                    OutlinedTextField(
                        value = if (portionCount == portionCount.toLong().toDouble())
                            portionCount.toLong().toString()
                        else portionCount.toString(),
                        onValueChange = {
                            viewModel.setPortionCount(it.toDoubleOrNull() ?: 1.0)
                        },
                        label = { Text("Nombre de ${selectedFood!!.portionUnit}s") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        supportingText = {
                            Text("1 ${selectedFood!!.portionUnit} = ${selectedFood!!.portionSize.toInt()} g")
                        }
                    )
                } else {
                    OutlinedTextField(
                        value = if (grams == grams.toLong().toDouble())
                            grams.toLong().toString()
                        else grams.toString(),
                        onValueChange = {
                            viewModel.setGrams(it.toDoubleOrNull() ?: 0.0)
                        },
                        label = { Text("Quantité (grammes)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        suffix = { Text("g") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Calorie Preview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryGreen.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Calories",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "${effectiveGrams.toInt()} g au total",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${calculatedCalories.toInt()} kcal",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Add Button
                Button(
                    onClick = {
                        // Here you would typically save the meal
                        // For now, just navigate back
                        onMealAdded()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    ),
                    enabled = calculatedCalories > 0
                ) {
                    Text("Ajouter le repas")
                }
            }
        }
    }
}

@Composable
fun FoodListItem(
    food: Food,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                food.country?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${food.caloriesPer100g.toInt()} kcal/100g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val kcalPerPortion = (food.caloriesPer100g * food.portionSize / 100).toInt()
                Text(
                    text = "≈ $kcalPerPortion kcal / ${food.portionUnit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
