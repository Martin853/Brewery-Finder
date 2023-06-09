package com.example.breweryfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.breweryfinder.databinding.ActivityMainBinding
import retrofit2.HttpException
import java.io.IOException
import com.example.breweryfinder.DataStorageManager
import com.google.gson.Gson


const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var breweryAdapter: BreweryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()

        lifecycleScope.launchWhenCreated {
            binding.progressBar.isVisible = true

            // Check if there is saved data
            val savedData = DataStorageManager.getData(this@MainActivity)
            if (savedData != null) {
                // Render the saved data
                val breweries = parseBreweries(savedData)
                breweryAdapter.breweries = breweries as MutableList<Brewery>
                binding.progressBar.isVisible = false
            } else {
                // Fetch data from API
                val response = try {
                    RetrofitInstance.api.getBreweries(6)
                } catch (e: IOException) {
                    Log.e(TAG, "IOException, you might not have an internet connection")
                    binding.progressBar.isVisible = false
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Log.e(TAG, "HttpException, unexpected response")
                    binding.progressBar.isVisible = false
                    return@launchWhenCreated
                }

                if (response.isSuccessful && response.body() != null) {
                    val breweries = response.body() as MutableList<Brewery>
                    breweryAdapter.breweries = breweries

                    // Save the fetched data
                    val dataToSave = convertBreweriesToString(breweries)
                    DataStorageManager.saveData(this@MainActivity, dataToSave)
                } else {
                    Log.e(TAG, "Response not successful")
                }

                binding.progressBar.isVisible = false
            }
        }
    }

    // Convert breweries to JSON string
    private fun convertBreweriesToString(breweries: List<Brewery>): String {
        val gson = Gson()
        return gson.toJson(breweries)
    }


    // Parse JSON string to breweries list
    private fun parseBreweries(data: String): List<Brewery> {
        val gson = Gson()
        val arrayType = Array<Brewery>::class.java
        return gson.fromJson(data, arrayType).toList()
    }

    // Setup the recycler view with the brewery adapter
    private fun setupRecyclerView() = binding.rvBreweries.apply {
        breweryAdapter = BreweryAdapter()
        adapter = breweryAdapter
        layoutManager = LinearLayoutManager(this@MainActivity)

    }
}