package no.uio.ifi.in2000.team20.team20app.data.local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caches 1991–2020 monthly temperature normals for a geographic area.
// locationKey format: "%.2f, %.2f".format(lat, lon) (~1km precision)
// monthlyMean/Max/Min: JSON-encoded List<Double> of 12 values (Jan=index 0)
@Entity(tableName = "temperature_cache")
data class TemperatureCacheEntity(
    @PrimaryKey val locationKey: String,
    val monthlyMean: String,
    val monthlyMax: String,
    val monthlyMin: String
)