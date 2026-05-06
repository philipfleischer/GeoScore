package no.uio.ifi.in2000.team20.team20app.data.local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caches the number of hazard events a location has been exposed to.
// locationKey format: "$roundedLat,$roundedLon" (2 decimal places)
@Entity(tableName = "exposure_cache")
data class ExposureCacheEntity(
    @PrimaryKey val locationKey: String,
    val eventCount: Int,
    val score: Double
)