package fr.patrickconti.specto.samples

import fr.patrickconti.specto.model.ArtCollection
import fr.patrickconti.specto.model.Artist
import fr.patrickconti.specto.model.Painting
import java.time.Year

class Samples {
    companion object {
        var sampleArtist = Artist(
            "Sample Artist",
            Year.parse("1834"),
            "This is the sample description for the sample artist."
        )
        var samplePainting = Painting(
            "Sample Painting",
            sampleArtist,
            "oil painting",
            "This is the sample description for the sample painting",
            Year.parse("1844"),
            "samplePainting.jpg",
            id = "com.ifndev.spectacles.samples.samplepainting",
            -1,
            -1,
            -1
        )
        var sampleCollection = ArtCollection(
            "Sample Collection",
            arrayListOf(),
            "sampledb"
        )
    }
}