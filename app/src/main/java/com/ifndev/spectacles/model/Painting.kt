package fr.patrickconti.specto.model

import java.time.Year

data class Painting(
    val title: String,
    val artist: Artist,
    val medium: String,
    val description: String,
    val year: Year,
    val photo: String,
    val id: String,
    val dbindex: Int,
    val imageindex: Int,
    val cardinality: Int
)