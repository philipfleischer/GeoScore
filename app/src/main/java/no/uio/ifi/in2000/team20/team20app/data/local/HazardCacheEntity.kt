package no.uio.ifi.in2000.team20.team20app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caches P95 precipitation and wind values for a geographic area.
// locationKey format: "$roundedLat,$roundedLon" (2 decimal places)
@Entity(tableName = "hazard_cache")
data class HazardCacheEntity(
    @PrimaryKey val locationKey: String,
    val p95Precipitation: Double,
    val p95Wind: Double
)