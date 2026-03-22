package com.kaloree.data.remote

import com.google.gson.annotations.SerializedName

data class OpenFoodFactsResponse(
    @SerializedName("products") val products: List<OFFProduct> = emptyList(),
    @SerializedName("count") val count: Int = 0
)

data class OFFProduct(
    @SerializedName("product_name") val productName: String? = null,
    @SerializedName("product_name_fr") val productNameFr: String? = null,
    @SerializedName("brands") val brands: String? = null,
    @SerializedName("quantity") val quantity: String? = null,
    @SerializedName("nutriments") val nutriments: OFFNutriments? = null,
    @SerializedName("serving_size") val servingSize: String? = null,
    @SerializedName("serving_quantity") val servingQuantity: Double? = null
) {
    val displayName: String
        get() {
            val name = productNameFr?.takeIf { it.isNotBlank() }
                ?: productName?.takeIf { it.isNotBlank() }
                ?: "Produit inconnu"
            return if (!brands.isNullOrBlank()) "$name ($brands)" else name
        }

    val kcalPer100g: Double
        get() = nutriments?.energyKcal100g
            ?: nutriments?.energyKj100g?.let { it / 4.184 }
            ?: 0.0
}

data class OFFNutriments(
    @SerializedName("energy-kcal_100g") val energyKcal100g: Double? = null,
    @SerializedName("energy-kj_100g") val energyKj100g: Double? = null,
    @SerializedName("energy-kcal_serving") val energyKcalServing: Double? = null,
    @SerializedName("proteins_100g") val proteins100g: Double? = null,
    @SerializedName("carbohydrates_100g") val carbs100g: Double? = null,
    @SerializedName("fat_100g") val fat100g: Double? = null
)
