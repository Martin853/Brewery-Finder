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
            val response = try {
                RetrofitInstance.api.getBreweries(6)
            } catch (e: IOException) {
                Log.e(TAG, "IOException, you might not have internet connection")
                binding.progressBar.isVisible = false
                return@launchWhenCreated

            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexcpected response")
                binding.progressBar.isVisible = false
                return@launchWhenCreated
            }

            if (response.isSuccessful && response.body() != null) {
                breweryAdapter.breweries = (response.body() as MutableList<Brewery>?)!!
            } else  {
                Log.e(TAG, "Response not successful")
            }

            binding.progressBar.isVisible = false
        }
    }

    private fun setupRecyclerView() = binding.rvBreweries.apply {
        breweryAdapter = BreweryAdapter()
        adapter = breweryAdapter
        layoutManager = LinearLayoutManager(this@MainActivity)

    }
}