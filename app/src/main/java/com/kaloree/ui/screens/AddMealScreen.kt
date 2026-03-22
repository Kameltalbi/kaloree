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
import androidx.compose.material.icons.filled.Delete
import com.kaloree.data.entity.Food
import com.kaloree.data.entity.MealType
import com.kaloree.data.repository.FoodSearchResult
import com.kaloree.ui.theme.PrimaryGreen
import com.kaloree.viewmodel.AddMealViewModel
import com.kaloree.viewmodel.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    onNavigateBack: () -> Unit,
    onMealAdded: () -> Unit,
    viewModel: AddMealViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val selectedFood by viewModel.selectedFood.collectAsStateWithLifecycle()
    val mealType by viewModel.mealType.collectAsStateWithLifecycle()
    val usePortions by viewModel.usePortions.collectAsStateWithLifecycle()
    val portionCount by viewModel.portionCount.collectAsStateWithLifecycle()
    val grams by viewModel.grams.collectAsStateWithLifecycle()
    val effectiveGrams by viewModel.effectiveGrams.collectAsStateWithLifecycle()
    val calculatedCalories by viewModel.calculatedCalories.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartTotalCalories by viewModel.cartTotalCalories.collectAsStateWithLifecycle()

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
            // Meal type selector
            Text(
                text = "Type de repas",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MealType.values().forEach { type ->
                    FilterChip(
                        selected = mealType == type,
                        onClick = { viewModel.setMealType(type) },
                        label = { Text("${type.emoji} ${type.label}") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::setSearchQuery,
                label = { Text("Rechercher un aliment") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Cart summary (shown when items added)
            if (cartItems.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryGreen.copy(alpha = 0.08f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${mealType.emoji} ${mealType.label} — ${cartItems.size} aliment(s)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${cartTotalCalories.toInt()} kcal",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        cartItems.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.food.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${item.grams.toInt()} g  •  ${item.calories.toInt()} kcal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                IconButton(
                                    onClick = { viewModel.removeCartItem(index) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Retirer",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Food List or Selected Food Details
            if (selectedFood == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sélectionne un aliment",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = PrimaryGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults) { result ->
                        FoodListItem(
                            result = result,
                            onClick = { viewModel.selectFood(result.food) }
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

                // Ajouter au panier
                Button(
                    onClick = { viewModel.confirmItem() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    enabled = calculatedCalories > 0
                ) {
                    Text("+ Ajouter au repas")
                }

                if (cartItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.saveMeal { onMealAdded() } },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Terminer (${cartItems.size} aliment(s) — ${cartTotalCalories.toInt()} kcal)")
                    }
                }
            }
        }
    }
}

@Composable
fun FoodListItem(
    result: FoodSearchResult,
    onClick: () -> Unit
) {
    val food = result.food
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = food.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    if (result.isOnline) {
                        Surface(
                            color = PrimaryGreen.copy(alpha = 0.15f),
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = "online",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryGreen,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
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
