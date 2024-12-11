package com.example.androidproject.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EntityList(entities: List<Entity>, onEntityClick: (Entity) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(entities) { entity ->
            EntityItem(entity = entity, onClick = { onEntityClick(entity) })
        }
    }
}

@Composable
fun EntityItem(entity: Entity, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp), elevation = 4.dp) {
        Column(modifier = Modifier.padding(16.dp).clickable { onClick() }) {
            Text(text = entity.title, style = MaterialTheme.typography.h6)
            Text(text = "Lat: ${entity.lat}, Lon: ${entity.lon}", style = MaterialTheme.typography.body2)
            // Display image if needed, or a placeholder
        }
    }
}
