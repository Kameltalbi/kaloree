package com.kaloree.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kaloree.data.dao.*
import com.kaloree.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Food::class, Meal::class, Activity::class, WeightLog::class],
    version = 3,
    exportSchema = false
)
abstract class KaloreeDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun foodDao(): FoodDao
    abstract fun mealDao(): MealDao
    abstract fun activityDao(): ActivityDao
    abstract fun weightLogDao(): WeightLogDao

    companion object {
        @Volatile
        private var INSTANCE: KaloreeDatabase? = null

        fun getDatabase(context: Context): KaloreeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KaloreeDatabase::class.java,
                    "kaloree_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.foodDao())
                }
            }
        }

        suspend fun populateDatabase(foodDao: FoodDao) {
            // Insert mock foods
            val mockFoods = listOf(
                Food(name = "Riz blanc", caloriesPer100g = 130.0, country = "Général"),
                Food(name = "Poulet (blanc)", caloriesPer100g = 165.0, country = "Général"),
                Food(name = "Pâtes", caloriesPer100g = 131.0, country = "Italie"),
                Food(name = "Couscous", caloriesPer100g = 112.0, country = "Maghreb"),
                Food(name = "Lablabi", caloriesPer100g = 85.0, country = "Tunisie"),
                Food(name = "Œuf (entier)", caloriesPer100g = 155.0, country = "Général"),
                Food(name = "Pain blanc", caloriesPer100g = 265.0, country = "Général"),
                Food(name = "Pomme", caloriesPer100g = 52.0, country = "Général"),
                Food(name = "Banane", caloriesPer100g = 89.0, country = "Général"),
                Food(name = "Lait entier", caloriesPer100g = 61.0, country = "Général"),
                Food(name = "Yaourt nature", caloriesPer100g = 59.0, country = "Général"),
                Food(name = "Fromage", caloriesPer100g = 350.0, country = "Général"),
                Food(name = "Saumon", caloriesPer100g = 208.0, country = "Général"),
                Food(name = "Avocat", caloriesPer100g = 160.0, country = "Général"),
                Food(name = "Huile d'olive", caloriesPer100g = 884.0, country = "Général"),
                Food(name = "Tomate", caloriesPer100g = 18.0, country = "Général"),
                Food(name = "Concombre", caloriesPer100g = 15.0, country = "Général"),
                Food(name = "Carotte", caloriesPer100g = 41.0, country = "Général"),
                Food(name = "Pomme de terre", caloriesPer100g = 77.0, country = "Général"),
                Food(name = "Thon", caloriesPer100g = 132.0, country = "Général")
            )
            foodDao.insertFoods(mockFoods)
        }
    }
}
