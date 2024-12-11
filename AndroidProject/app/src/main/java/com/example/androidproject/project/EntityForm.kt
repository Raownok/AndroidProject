package com.example.androidproject.project

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityForm(entity: Entity? = null, onSubmit: (Entity) -> Unit) {
    var title by remember { mutableStateOf(entity?.title ?: "") }
    var lat by remember { mutableStateOf(entity?.lat?.toString() ?: "") }
    var lon by remember { mutableStateOf(entity?.lon?.toString() ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Title input
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        // Latitude input
        TextField(
            value = lat,
            onValueChange = { lat = it },
            label = { Text("Latitude") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Longitude input
        TextField(
            value = lon,
            onValueChange = { lon = it },
            label = { Text("Longitude") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Image Picker button
        Button(onClick = {
            // Implement image picking logic
        }) {
            Text("Pick Image")
        }

        // Submit button
        Button(
            onClick = {
                val newEntity = Entity(
                    id = entity?.id ?: 0, // If editing, retain existing ID
                    title = title,
                    lat = lat.toDouble(),
                    lon = lon.toDouble(),
                    image = imageUri?.toString() ?: ""
                )
                onSubmit(newEntity) // Submit entity
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Submit")
        }
    }
}
