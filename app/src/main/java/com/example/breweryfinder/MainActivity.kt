package com.example.breweryfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.breweryfinder.databinding.ActivityMainBinding
import retrofit2.HttpException
import java.io.IOException
import com.example.breweryfinder.DataStorageManager
import com.google.gson.Gson


const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var breweryAdapter: BreweryAdapter
    private var page = 1
    private var isLoading = false
    private var isLastPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()

        loadData()

        binding.rvBreweries.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
                    // Reached the end of the list
                    if (!isLoading && !isLastPage) {
                        page++
                        loadData()
                    }
                }
            }
        })
    }

    private fun loadData() {
        lifecycleScope.launchWhenCreated {
            if (page == 1) {
                binding.progressBar.isVisible = true
            }

            val response = try {
                RetrofitInstance.api.getBreweries(6)
            } catch (e: IOException) {
                Log.e(TAG, "IOException, you might not have an internet connection")
                handleDataLoadError()
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response")
                handleDataLoadError()
                return@launchWhenCreated
            }

            if (response.isSuccessful && response.body() != null) {
                val breweries = response.body() as MutableList<Brewery>
                if (page == 1) {
                    breweryAdapter.breweries = breweries
                    DataStorageManager.saveData(this@MainActivity, convertBreweriesToString(breweries))
                } else {
                    breweryAdapter.addBreweries(breweries)
                    isLoading = false
                    if (breweries.isEmpty()) {
                        isLastPage = true
                    }
                }
            } else {
                Log.e(TAG, "Response not successful")
                handleDataLoadError()
            }

            binding.progressBar.isVisible = false
        }
    }

    private fun handleDataLoadError() {
        if (page == 1) {
            binding.progressBar.isVisible = false
        } else {
            isLoading = false
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