package com.example.breweryfinder
import android.content.Context
import android.content.SharedPreferences

object DataStorageManager {
    // Constant declaration
    private const val PREFERENCE_NAME = "BreweryFinderPreferences"
    private const val DATA_KEY = "apiData"

    // Save data to shared preferences function
    fun saveData(context: Context, data: String) {
        val sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(DATA_KEY, data)
        editor.apply()
    }

    // Retrieve data function
    fun getData(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(DATA_KEY, null)
    }
}
