package fr.patrickconti.specto.model

import java.time.Year

data class Artist constructor(
    val name: String, val year: Year, val description: String
)