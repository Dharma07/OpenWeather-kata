package com.training.kotlin.openweatherkata.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.training.kotlin.openweatherkata.MainActivity
import com.training.kotlin.openweatherkata.MainViewModel
import com.training.kotlin.openweatherkata.adapter.ListWeatherAdapter
import com.training.kotlin.openweatherkata.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var listAdapter: ListWeatherAdapter
    lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = (activity as MainActivity).mainViewModel

        setupRecyclerView()

        viewModel.getAllWeatherData()
        getListFromDb()

        return root
    }

    private fun getListFromDb() {

        viewModel._fetchLocalData.observe(viewLifecycleOwner, Observer { response ->
            if (response != null)
                listAdapter.differ.submitList(response.toList())
        })
    }

    private fun setupRecyclerView() {

        listAdapter = ListWeatherAdapter()
        binding.weatherList.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}