package fr.patrickconti.specto.model

data class ArtCollection(
    val title: String,
    val paintings: ArrayList<Painting>,
    val dbname: String
) {
    // add two collections
    operator fun plus(other: ArtCollection): ArtCollection {
        val newPaintings = ArrayList<Painting>()
        newPaintings.addAll(this.paintings)
        newPaintings.addAll(other.paintings)
        return ArtCollection(this.title, newPaintings, this.dbname)
    }

    fun add(painting: Painting) {
        this.paintings.add(painting)
    }
}