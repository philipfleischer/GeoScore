package no.uio.ifi.in2000.team20.team20app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caches 1991–2020 monthly snow depth normals for a geographic area.
// locationKey format: "%.2f, %.2f".format(lat, lon) (~1km precision)
// monthlyMean/Max: JSON-encoded List<Double> of 12 values (Jan=index 0)
@Entity(tableName = "snow_cache")
data class SnowCacheEntity(
    @PrimaryKey val locationKey: String,
    val monthlyMean: String,
    val monthlyMax: String
)
