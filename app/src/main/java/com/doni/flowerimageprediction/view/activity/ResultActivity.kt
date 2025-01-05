package com.doni.flowerimageprediction.view.activity

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.doni.flowerimageprediction.R
import com.doni.flowerimageprediction.databinding.ActivityResultBinding
import com.doni.flowerimageprediction.helper.ImageClassifier
import com.doni.flowerimageprediction.model.entity.ResultEntity
import com.doni.flowerimageprediction.view.ViewModelFactory
import com.doni.flowerimageprediction.view.viewmodel.HistoryViewModel
import com.doni.flowerimageprediction.view.viewmodel.ResultViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifier: ImageClassifier

    private lateinit var viewModel: ResultViewModel

    val date = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        viewModel = viewModels<ResultViewModel> { factory }.value

        imageClassifier = ImageClassifier("model.tflite", assets)

        val imageUri = intent.getStringExtra(EXTRA_CAMERAX_IMAGE)?.toUri()
        binding.resultImage.setImageURI(imageUri)
        Log.d("ResultActivity", "Image URI: $imageUri")
        if (imageUri != null) {
            processImage(imageUri)
        } else {
            Log.e("ResultActivity", "Image URI is null.")
        }
    }

    private fun processImage(imageUri: Uri) {
        Log.d("ResultActivity", "Processing image...")

        lifecycleScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    val inputStream = contentResolver.openInputStream(imageUri)
                    BitmapFactory.decodeStream(inputStream)
                }
                bitmap?.let {
                    val result = withContext(Dispatchers.Default) {
                        imageClassifier.predict(it)
                    }

                    if (result == null){
                        binding.saveButton.isEnabled = false
                    } else {
                        binding.saveButton.isEnabled = true
                        binding.saveButton.setOnClickListener {
                            lifecycleScope.launch {
                                try {
                                    val resultEntity = ResultEntity(
                                        date = date,
                                        resultText = result,
                                        imageUri = imageUri.toString()
                                    )
                                    viewModel.saveResult(resultEntity)
                                    Toast.makeText(this@ResultActivity, "Result saved successfully", Toast.LENGTH_SHORT).show()
                                    finish()
                                } catch (e: Exception) {
                                    Toast.makeText(this@ResultActivity, "Error saving result: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    binding.resultText.text = "Analysis Result: " + result
                    binding.dateTextView.text = "Analysis taken on " + date
                    Log.d("ResultActivity", "Image classification result: $result")
                } ?: run {
                    Log.e("ResultActivity", "Bitmap is null.")
                }
            } catch (e: Exception) {
                Log.e("ResultActivity", "Error processing image: ${e.message}")
            }
        }
    }

    companion object {
        const val EXTRA_CAMERAX_IMAGE = "extra_camerax_image"
    }
}