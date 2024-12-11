package com.example.androidproject

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.androidproject.project.Entity
import com.example.androidproject.project.EntityForm
import com.example.androidproject.project.EntityList
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Mapbox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.painter.painterResource
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.androidproject.project.ApiResponse
import network.ApiClient
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.FileOutputStream
import androidx.compose.ui.tooling.preview.Preview as Preview1

class MainActivity : ComponentActivity() {
    private val entities = mutableListOf<Entity>() // Store entities locally
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var editingEntity by remember { mutableStateOf<Entity?>(null) }
            var entitiesList by remember { mutableStateOf(entities) }

            // Fetch entities from the API when the app starts
            fetchEntitiesFromApi()

            if (editingEntity != null) {
                // Show form to edit the entity
                EntityForm(entity = editingEntity, onSubmit = { entity ->
                    entitiesList = entitiesList.map {
                        if (it.id == entity.id) entity else it
                    }.toMutableList() // Update the entity in the list
                    editingEntity = null // Close the form
                })
            } else {
                // Show list of entities
                EntityList(entities = entitiesList) { entity ->
                    editingEntity = entity // Start editing when clicked
                }
            }

            // Update markers on the map after fetching data
            updateMapMarkers(entities)
        }
    }

    // Fetch entities from API
    private fun fetchEntitiesFromApi() {
        ApiClient.apiService.getEntities().enqueue(object : Callback<List<Entity>> {
            override fun onResponse(call: Call<List<Entity>>, response: Response<List<Entity>>) {
                if (response.isSuccessful) {
                    // Update entities list with fetched data
                    val fetchedEntities = response.body() ?: emptyList()
                    entities.clear()
                    entities.addAll(fetchedEntities)
                } else {
                    Log.e("API", "Error fetching entities: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Entity>>, t: Throwable) {
                Log.e("API", "Failed to fetch entities", t)
            }
        })
    }

    // Mapbox Marker Implementation
    @Composable
    fun updateMapMarkers(entities: List<Entity>) {
        val mapView = rememberMapView()

        LaunchedEffect(entities) {
            mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
                val annotationManager: PointAnnotationManager = mapView.annotations.createPointAnnotationManager()

                // Clear previous markers
                annotationManager.deleteAll()

                // Add a new marker for each entity
                entities.forEach { entity ->
                    val point = PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(entity.lon, entity.lat)) // Set marker position
                        .withTextField(entity.title) // Set marker title

                    annotationManager.create(point) // Create marker on map
                }
            }
        }

        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )
    }

    // Initialize Mapbox MapView
    @Composable
    fun rememberMapView(): MapView {
        return remember { MapView(LocalContext.current) }
    }

    // Method to initialize CameraX and start capturing images
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview use case
            val preview = androidx.camera.core.Preview.Builder().build()

            // ImageCapture use case
            imageCapture = ImageCapture.Builder().build()

            // Set up CameraX with preview and image capture use cases
            cameraProvider.bindToLifecycle(
                this, lifecycle, preview, imageCapture
            )
        }, ContextCompat.getMainExecutor(this))
    }

    // Take photo and handle file storage
    private fun takePhoto() {
        val photoFile = createFile(applicationContext)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d("CameraX", "Photo saved to $savedUri")
                    val resizedImage = resizeImage(savedUri)
                    uploadImage(resizedImage)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
                }
            })
    }

    // Resize the image to 800x600
    private fun resizeImage(imageUri: Uri): Bitmap {
        val originalBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
        return Bitmap.createScaledBitmap(originalBitmap, 800, 600, false)
    }

    // Upload the resized image using Retrofit
    private fun uploadImage(resizedBitmap: Bitmap) {
        val file = File(applicationContext.cacheDir, "resized_image.jpg")
        val outputStream = FileOutputStream(file)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        outputStream.flush()
        outputStream.close()

        // Now you can use Retrofit to upload the file
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestBody)

        ApiClient.apiService.createEntity(title, lat, lon, body).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Log.d("Image Upload", "Image uploaded successfully")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("Image Upload", "Image upload failed", t)
            }
        })
    }

    // Create a file for storing the photo
    private fun createFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    @Preview1(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MapScreen()
    }
}
