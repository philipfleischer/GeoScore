package no.uio.ifi.in2000.team20.team20app.data.local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caches the final combined risk score for a location.
// locationKey format: "$roundedLat,$roundedLon" (2 decimal places)
@Entity(tableName = "total_score_cache")
data class TotalScoreCacheEntity(
    @PrimaryKey val locationKey: String,
    val hazardScore: Double,
    val vulnerabilityScore: Double,
    val exposureScore: Double,
    val geoScore: Double
)