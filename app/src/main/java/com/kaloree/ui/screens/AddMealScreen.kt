package com.kaloree.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
        },
        bottomBar = {
            if (cartItems.isNotEmpty() && selectedFood == null) {
                Surface(shadowElevation = 8.dp) {
                    Button(
                        onClick = { viewModel.saveMeal { onMealAdded() } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "✓ Enregistrer le repas — ${cartTotalCalories.toInt()} kcal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Meal type selector ──────────────────────────────────
            item {
                Text(
                    text = "Type de repas",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MealType.values().forEach { type ->
                        val selected = mealType == type
                        Card(
                            onClick = { viewModel.setMealType(type) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selected) PrimaryGreen else MaterialTheme.colorScheme.surface
                            ),
                            border = if (!selected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
                            elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 4.dp else 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = type.emoji, fontSize = 20.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = type.label.split("-").first().trim(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // ── Cart summary ────────────────────────────────────────
            if (cartItems.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.07f)),
                        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = mealType.emoji, fontSize = 16.sp)
                                    Text(
                                        text = mealType.label,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = PrimaryGreen
                                    ) {
                                        Text(
                                            text = "${cartItems.size}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${cartTotalCalories.toInt()} kcal",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            cartItems.forEachIndexed { index, item ->
                                if (index > 0) HorizontalDivider(thickness = 0.5.dp, color = PrimaryGreen.copy(alpha = 0.15f))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.food.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "${item.grams.toInt()} g",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "${item.calories.toInt()} kcal",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryGreen
                                    )
                                    IconButton(
                                        onClick = { viewModel.removeCartItem(index) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Retirer",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Search bar ──────────────────────────────────────────
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::setSearchQuery,
                    placeholder = { Text("Rechercher un aliment...") },
                    leadingIcon = {
                        if (isSearching)
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = PrimaryGreen)
                        else
                            Icon(Icons.Default.Search, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }

            // ── Selected food detail ─────────────────────────────────
            if (selectedFood != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedFood!!.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = PrimaryGreen.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = "${selectedFood!!.caloriesPer100g.toInt()} kcal / 100g",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = PrimaryGreen,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                TextButton(onClick = { viewModel.clearSelection() }) {
                                    Text("Changer", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(Modifier.height(14.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(14.dp))

                            // Segmented toggle: Portions vs Grammes
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                listOf(
                                    true to "${selectedFood!!.portionUnit.replaceFirstChar { it.uppercase() }}s",
                                    false to "Grammes"
                                ).forEach { (isPortion, label) ->
                                    val sel = usePortions == isPortion
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (sel) PrimaryGreen else Color.Transparent)
                                            .then(Modifier)
                                    ) {
                                        TextButton(
                                            onClick = { viewModel.setUsePortions(isPortion) },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = label,
                                                color = if (sel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            if (usePortions) {
                                OutlinedTextField(
                                    value = if (portionCount == portionCount.toLong().toDouble())
                                        portionCount.toLong().toString() else portionCount.toString(),
                                    onValueChange = { viewModel.setPortionCount(it.toDoubleOrNull() ?: 1.0) },
                                    label = { Text("Nombre de ${selectedFood!!.portionUnit}s") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(12.dp),
                                    supportingText = {
                                        Text(
                                            "1 ${selectedFood!!.portionUnit} = ${selectedFood!!.portionSize.toInt()} g",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                )
                            } else {
                                OutlinedTextField(
                                    value = if (grams == grams.toLong().toDouble())
                                        grams.toLong().toString() else grams.toString(),
                                    onValueChange = { viewModel.setGrams(it.toDoubleOrNull() ?: 0.0) },
                                    label = { Text("Quantité") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(12.dp),
                                    suffix = { Text("g") }
                                )
                            }

                            Spacer(Modifier.height(14.dp))

                            // Calorie preview row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PrimaryGreen.copy(alpha = 0.08f))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Apport calorique",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${effectiveGrams.toInt()} g au total",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${calculatedCalories.toInt()} kcal",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                            }

                            Spacer(Modifier.height(14.dp))

                            Button(
                                onClick = { viewModel.confirmItem() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                shape = RoundedCornerShape(14.dp),
                                enabled = calculatedCalories > 0
                            ) {
                                Text("+ Ajouter au repas", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // ── Food search results ──────────────────────────────
                items(searchResults) { result ->
                    FoodListItem(
                        result = result,
                        onClick = { viewModel.selectFood(result.food) }
                    )
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
    val kcalPerPortion = (food.caloriesPer100g * food.portionSize / 100).toInt()
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
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
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (result.isOnline) {
                        Surface(
                            color = Color(0xFF2196F3).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "web",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF2196F3),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "${food.caloriesPer100g.toInt()} kcal / 100g  •  ${food.portionSize.toInt()} g / ${food.portionUnit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = PrimaryGreen.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "≈ $kcalPerPortion kcal",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}
