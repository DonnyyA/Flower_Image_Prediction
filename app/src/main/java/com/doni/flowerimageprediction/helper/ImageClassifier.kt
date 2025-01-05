package com.doni.flowerimageprediction.helper

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ImageClassifier(private val modelPath: String, private val assetManager: android.content.res.AssetManager) {

    private var interpreter: Interpreter
    private val inputSize = 224 // Input image size (224x224 for your model)
    private val classes = listOf("tulip", "daisy", "dandelion", "rose", "sunflower")

    init {
        val model = loadModelFile(assetManager, modelPath)
        interpreter = Interpreter(model)
    }

    fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(inputSize * inputSize)
        resizedBitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in pixels) {
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }
        return byteBuffer
    }

    fun predict(bitmap: Bitmap): String {
        val inputBuffer = preprocessImage(bitmap)
        val outputBuffer = Array(1) { FloatArray(classes.size) }

        interpreter.run(inputBuffer, outputBuffer)

        outputBuffer.forEach {
            Log.d("Output Buffer", it[0].toString())
            Log.d("Output Buffer", it[1].toString())
            Log.d("Output Buffer", it[2].toString())
            Log.d("Output Buffer", it[3].toString())
            Log.d("Output Buffer", it[4].toString())
        }

        val predictedIndex = outputBuffer[0].indices.maxByOrNull { outputBuffer[0][it] } ?: -1
        val maxConfidence = outputBuffer[0][predictedIndex]
        if (maxConfidence < 0.8) {
            return "Unknown Flower"
        }

        return classes[predictedIndex]
    }
}