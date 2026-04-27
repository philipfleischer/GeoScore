package no.uio.ifi.in2000.team20.team20app.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_locations")
data class SavedLocationEntity(
    @PrimaryKey
    val address: String,
    val name: String,
    val municipality: String?,
    val county: String?,
    val lat: Double,
    val lon: Double
)