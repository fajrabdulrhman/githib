package com.example.weatherapp.ui.fragments

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherapp.R
import com.example.weatherapp.adapters.DayAdapter
import com.example.weatherapp.adapters.SavedAdapter
import com.example.weatherapp.adapters.SearchAdapter
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.ui.ViewModelProviderFactory
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.util.Resource
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SavedCitiesFragment : Fragment(R.layout.fragment_saved_cities2) {
    lateinit var searchButton: FloatingActionButton
    lateinit var savedAdapter: SavedAdapter
    lateinit var savedRecyclerView: RecyclerView
    private lateinit var viewModel: WeatherViewModel
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchBar: EditText
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var dialog: BottomSheetDialog
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout 


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weatherRepository = WeatherRepository(WeatherDatabase(requireContext()))
        val application = requireActivity().application
        val viewModelProviderFactory = ViewModelProviderFactory(application, weatherRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(WeatherViewModel::class.java)

        savedRecyclerView = view.findViewById(R.id.savedRecyclerView)
        searchButton = view.findViewById(R.id.floatingActionButton)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        setupRecyclerView()
        showBottomSheet()  // Setup the BottomSheetDialog

        searchButton.setOnClickListener {
            dialog.show()
        }
        swipeRefreshLayout.setOnRefreshListener {
            getCountry()
        }

     savedAdapter.setOnItemClickListener {
         val bundle = Bundle().apply {
             putSerializable("weather",it)
         }
         findNavController().navigate(R.id.action_savedCitiesFragment_to_detailsFragment,
             bundle
         )
     }

        setupSearchFunctionality()  // Setup search functionality


        //function to swipe and delete
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val weather=savedAdapter.differ.currentList[position]
                viewModel.deleteCounty(weather)
                Snackbar.make(view, "Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveWeather(weather)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(savedRecyclerView)
        }

        viewModel.getSavedWeather().observe(viewLifecycleOwner, Observer { weatherResponse ->
            savedAdapter.differ.submitList(weatherResponse)

        })
    }

    private fun setupRecyclerView() {
        savedAdapter = SavedAdapter()
        savedRecyclerView.apply {
            adapter = savedAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun showBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.fragment_search, null)
        dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        dialog.setContentView(dialogView)

        // Initialize BottomSheet views
        searchBar = dialogView.findViewById(R.id.etSearch)  // Replace with actual ID of search EditText in fragment_search.xml
        searchRecyclerView = dialogView.findViewById(R.id.searchRV)  // Replace with actual ID of RecyclerView in fragment_search.xml

        setupSearchRecyclerView()
    }

    private fun setupSearchRecyclerView() {
        searchAdapter = SearchAdapter()
        searchRecyclerView.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            Log.d("searchRecyclerView", "$searchAdapter")
        }
    }

    private fun setupSearchFunctionality() {
        var job: Job? = null
        searchBar.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchWeather(editable.toString())
                    }
                }
            }
        }

//        searchAdapter.setOnItemClickListener { searchResponseItem ->
//            // Initiate the API call here
//            viewModel.getWeather(searchResponseItem.lat, searchResponseItem.lon)
//
//            viewModel.gettingWeather.observe(viewLifecycleOwner, Observer { response ->
//                when (response) {
//                    is Resource.Success -> {
//                        response.data?.let { weatherResponse ->
//                            Log.d("SavedCitiesFragment", "Weather data fetched: $weatherResponse")
//                            Toast.makeText(requireContext(), "Saved successfully", Toast.LENGTH_LONG).show()
//                            viewModel.saveWeather(weatherResponse)
//                        }
//                        dialog.dismiss()  // Dismiss the BottomSheetDialog
//                    }
//                    is Resource.Error -> {
//                        Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_LONG).show()
//                    }
//                    is Resource.Loading -> {
//                        Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_LONG).show()
//                    }
//                }
//            })
//        }


        viewModel.searchWeather.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { searchResponse ->
                        searchAdapter.differ.submitList(searchResponse)
                        Log.d("SearchFragment", "Weather data fetched: $searchResponse")
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

private fun getCountry() {
    searchAdapter.setOnItemClickListener { searchResponseItem ->
        // Initiate the API call here
        viewModel.getWeather(searchResponseItem.lat, searchResponseItem.lon)

        viewModel.gettingWeather.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { weatherResponse ->
                        Log.d("SavedCitiesFragment", "Weather data fetched: $weatherResponse")
                        Toast.makeText(requireContext(), "Saved successfully", Toast.LENGTH_LONG)
                            .show()
                        viewModel.saveWeather(weatherResponse)
                    }
                    dialog.dismiss()  // Dismiss the BottomSheetDialog
                }

                is Resource.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${response.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
}