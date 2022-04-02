package com.ifndev.spectacles

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skydoves.landscapist.glide.GlideImage
import fr.patrickconti.specto.model.Painting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaintingCard(navController: NavController, painting: Painting) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            .clickable {
                navController.navigate("painting/" + painting.id)
                Log.e("foo", "bar")
            },
        elevation = CardDefaults.cardElevation(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GlideImage(
                imageModel = "file:///android_asset/image_databases/" + painting.photo,
                contentDescription = painting.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
            ) {
                Text(text = painting.title, style = MaterialTheme.typography.titleSmall)
                Text(text = painting.artist.name, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}