# Kaloree - Application Android de Suivi Nutritionnel

Kaloree est une application Android moderne pour le suivi nutritionnel et la gestion du poids, développée avec **Kotlin** et **Jetpack Compose**.

## ✨ Fonctionnalités

- 📊 **Tableau de bord** avec suivi des calories consommées, brûlées et restantes
- 🍎 **Ajout de repas** avec recherche d'aliments et calcul automatique des calories
- 🏃 **Suivi des activités physiques** (marche, course, natation, musculation)
- 📈 **Historique** des repas et activités par jour
- ⚖️ **Suivi du poids** avec historique et graphique d'évolution
- 👤 **Profil utilisateur** avec calcul automatique du BMR et objectifs caloriques
- 🎨 **Interface moderne** et minimaliste avec Material Design 3

## 🛠️ Stack Technique

- **Langage**: Kotlin
- **UI**: Jetpack Compose avec Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Base de données**: Room (SQLite)
- **Navigation**: Navigation Compose
- **State Management**: ViewModel + StateFlow
- **Build**: Gradle avec Kotlin DSL

## 📱 Écrans

1. **Onboarding** - Configuration initiale du profil (sexe, âge, poids, taille, objectif)
2. **Dashboard** - Vue d'ensemble des calories et accès rapide aux fonctions
3. **AddMeal** - Recherche et ajout d'aliments avec calcul automatique
4. **AddActivity** - Enregistrement des activités physiques
5. **History** - Historique des repas et activités par jour
6. **Weight** - Suivi et historique du poids
7. **Profile** - Gestion du profil et paramètres

## 🚀 Calculs Implémentés

### BMR (Métabolisme Basal)
```
BMR = 10 × poids(kg) + 6.25 × taille(cm) - 5 × âge + s
où s = +5 (homme) ou -161 (femme)
```

### Objectifs Caloriques
- **Perte de poids**: BMR × 1.2 - 500 kcal
- **Maintien**: BMR × 1.2
- **Prise de masse**: BMR × 1.2 + 300 kcal

### Calories d'Activité
Utilisation des valeurs MET (Metabolic Equivalent of Task):
- Marche: 3.5 MET
- Course: 9.0 MET
- Natation: 8.0 MET
- Musculation: 4.0 MET
- Vélo: 7.0 MET

## 📦 Structure du Projet

```
app/src/main/java/com/kaloree/
├── app/
│   ├── KaloreeApplication.kt
│   └── MainActivity.kt
├── data/
│   ├── KaloreeDatabase.kt
│   ├── dao/
│   │   ├── ActivityDao.kt
│   │   ├── FoodDao.kt
│   │   ├── MealDao.kt
│   │   ├── UserDao.kt
│   │   └── WeightLogDao.kt
│   └── entity/
│       ├── Activity.kt
│       ├── Food.kt
│       ├── Meal.kt
│       ├── User.kt
│       └── WeightLog.kt
├── ui/
│   ├── navigation/
│   │   ├── KaloreeNavigation.kt
│   │   └── Screen.kt
│   ├── screens/
│   │   ├── AddActivityScreen.kt
│   │   ├── AddMealScreen.kt
│   │   ├── DashboardScreen.kt
│   │   ├── HistoryScreen.kt
│   │   ├── OnboardingScreen.kt
│   │   ├── ProfileScreen.kt
│   │   └── WeightScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── util/
│   └── CalorieCalculator.kt
└── viewmodel/
    ├── AddActivityViewModel.kt
    ├── AddMealViewModel.kt
    ├── DashboardViewModel.kt
    ├── HistoryViewModel.kt
    ├── OnboardingViewModel.kt
    ├── ProfileViewModel.kt
    └── WeightViewModel.kt
```

## 🥗 Aliments de Base (Mock Data)

- Riz blanc (130 kcal/100g)
- Poulet blanc (165 kcal/100g)
- Pâtes (131 kcal/100g)
- Couscous (112 kcal/100g)
- Lablabi (85 kcal/100g)
- Œuf entier (155 kcal/100g)
- Pain blanc (265 kcal/100g)
- Et 13 autres aliments...

## 🛠️ Compilation

### Prérequis
- Android Studio Hedgehog (2023.1.1) ou supérieur
- JDK 17 ou supérieur
- SDK Android 34

### Étapes

1. **Cloner le projet**:
```bash
git clone <repository-url>
cd Kaloree
```

2. **Télécharger le Gradle Wrapper** (si nécessaire):
```bash
./gradlew wrapper --gradle-version 8.7
```

3. **Compiler le projet**:
```bash
./gradlew assembleDebug
```

4. **Installer sur un appareil**:
```bash
./gradlew installDebug
```

### Alternative avec Android Studio

1. Ouvrir le projet dans Android Studio
2. Synchroniser le projet avec Gradle (Sync Now)
3. Lancer l'application avec le bouton "Run" (Shift+F10)

## 📝 Notes

- La base de données Room est initialisée avec des aliments de base au premier lancement
- Les calculs de calories utilisent des estimations MET standard
- L'application supporte le mode sombre automatique
- Interface responsive adaptée aux différentes tailles d'écran

## 🔧 Dépendances Principales

```kotlin
// Jetpack Compose
implementation(platform(libs.androidx.compose.bom))
implementation(libs.androidx.material3)
implementation(libs.androidx.navigation.compose)

// Architecture
implementation(libs.androidx.lifecycle.viewmodel.compose)

// Database
implementation(libs.androidx.room.runtime)
implementation(libs.androidx.room.ktx)
ksp(libs.androidx.room.compiler)
```

## 📄 Licence

Ce projet est sous licence MIT.

---

Développé avec ❤️ pour un suivi nutritionnel simplifié.
