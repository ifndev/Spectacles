package com.ifndev.spectacles.viewModel

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import fr.patrickconti.specto.model.Artist
import fr.patrickconti.specto.model.Painting
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.time.Year

class ActiveDatabaseViewModel {
    companion object {
        fun getPaintingIdFromIndexFromJson(json: String, index: Int): String {
            val jsonArray = JSONArray(json)
            // if there is a painting with the cardinality equal to index, return it's id
            for (i in 0 until jsonArray.length()) {
                if (jsonArray.getJSONObject(i).getInt("cardinality") == index) {
                    return jsonArray.getJSONObject(i).getString("id")
                }
            }

            return "missingno"
        }
    }

    val activeDatabasePaintingsViewModel = mutableStateMapOf<String, Painting>()
    val activeDatabaseFileName = mutableStateOf<String>("default.imgdb")

    fun getPainting(id: String): Painting {
        return activeDatabasePaintingsViewModel[id] ?: Painting(
            title = "unidentified painting",
            artist = Artist(
                "missingno",
                description = "unidentified artist",
                year = Year.now()
            ),
            medium = "none",
            description = "no description",
            year = Year.now(),
            photo = "samplePainting.jpg",
            id = "missingno",
            dbindex = -1,
            imageindex = -1,
            cardinality = -1,
        )
    }

    fun loadDatabaseFromJson(json: String, index: Int, context: Context) {

        activeDatabaseFileName.value = JSONArray(json).getJSONObject(index).getString("dbfile")
        val jsonArray = JSONArray(json).getJSONObject(index).getJSONArray("paintings")
        for (i in 0 until jsonArray.length()) {
            val element = jsonArray.getJSONObject(i)
            val painting = Painting(
                title = element.getString("title"),
                artist = Artist(element.getString("artist"), Year.now(), "no description"),
                medium = element.getString("medium"),
                description = element.getString("description"),
                year = Year.parse(element.getString("year")),
                photo = element.getString("photo"),
                id = element.getString("id"),
                cardinality = element.getInt("cardinality"),
                dbindex = index,
                imageindex = i
            )
            activeDatabasePaintingsViewModel[element.getString("id")] = painting
        }
    }

    fun getDatabaseAsJson(): String {
        val json = JSONArray()
        for (painting in activeDatabasePaintingsViewModel.values) {
            val jsonObject = JSONObject()
            jsonObject.put("title", painting.title)
            jsonObject.put("artist", painting.artist.name)
            jsonObject.put("medium", painting.medium)
            jsonObject.put("description", painting.description)
            jsonObject.put("year", painting.year.toString())
            jsonObject.put("photo", painting.photo)
            jsonObject.put("id", painting.id)
            jsonObject.put("cardinality", painting.cardinality)
            json.put(jsonObject)
        }
        return json.toString()
    }

    fun loadDatabaseFromFile(filename: String, index: Int, context: Context) {
        val ist: InputStream = context.assets.open(filename)
        val size: Int = ist.available()
        val buffer = ByteArray(size)
        ist.read(buffer)
        ist.close()
        val json = String(buffer, Charsets.UTF_8)

        loadDatabaseFromJson(json, index, context)
    }


}