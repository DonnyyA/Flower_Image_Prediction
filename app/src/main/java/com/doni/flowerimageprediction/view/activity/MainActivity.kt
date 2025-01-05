package com.doni.flowerimageprediction.view.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Camera
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.doni.flowerimageprediction.R
import com.doni.flowerimageprediction.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var cameraControl: CameraControl? = null

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val intent = Intent(this@MainActivity, ResultActivity::class.java)
            intent.putExtra(ResultActivity.EXTRA_CAMERAX_IMAGE, uri.toString())
            startActivity(intent)
        } else {
            Log.d("PerspectiveActivity", "Tidak ada gambar yang dipilih")
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionsGranted = permissions.entries.all { it.value }
        if (allPermissionsGranted) {
            startCamera()
        } else {
            Toast.makeText(
                this,
                R.string.permissions_denied_message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!isPermissionsGranted()){
            requestPermissions()
        }
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        startCamera()

        binding.galleryBtn.setOnClickListener {
            openGallery()
        }
        binding.captureImage.setOnClickListener {
            takePhoto()
        }
        binding.history.setOnClickListener {
            openHistory()
        }
    }

    private fun requestPermissions() {
        if (isPermissionsGranted()) {
            startCamera()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun isPermissionsGranted(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val storagePermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        return cameraPermission && storagePermission
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun openHistory() {
        val intent = Intent(this@MainActivity, HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createCustomTempFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val intent = Intent(this@MainActivity, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_CAMERAX_IMAGE, output.savedUri.toString())
                    Log.d("MainActivity", "Uri gambar camera : ${output.savedUri.toString()}")
                    startActivity(intent)
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@MainActivity,
                        R.string.failed_take_picture,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "onError: ${exc.message}")
                }
            }
        )
    }

    fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpg", filesDir)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            try {
                cameraProvider.unbindAll()

                val camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                cameraControl = camera.cameraControl

            } catch (exc: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    R.string.failed_show_camera_preview,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    companion object {
        private const val TAG = "Main Activity"
    }
}