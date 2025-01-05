package com.doni.flowerimageprediction.view.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.doni.flowerimageprediction.R
import com.doni.flowerimageprediction.databinding.ActivityHistoryBinding
import com.doni.flowerimageprediction.view.ViewModelFactory
import com.doni.flowerimageprediction.view.adapter.HistoryAdapter
import com.doni.flowerimageprediction.view.viewmodel.HistoryViewModel

class HistoryActivity : AppCompatActivity() {
    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adapter = HistoryAdapter()

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        viewModel = viewModels<HistoryViewModel> { factory }.value

        binding.rvHistory.adapter = adapter
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        viewModel.allResult.observe(this) {
            adapter.submitList(it)
            if (it.isEmpty()) {
                binding.rvHistory.visibility = View.GONE
                binding.emptyHistoryText.visibility = View.VISIBLE
            } else {
                binding.rvHistory.visibility = View.VISIBLE
                binding.emptyHistoryText.visibility = View.GONE
            }
        }
    }
}