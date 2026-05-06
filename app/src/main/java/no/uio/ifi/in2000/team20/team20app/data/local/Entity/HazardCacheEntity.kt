package no.uio.ifi.in2000.team20.team20app.data.local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caches extreme weather intensity scores for a geographic area.
// locationKey format: "$roundedLat,$roundedLon" (2 decimal places)
@Entity(tableName = "hazard_cache")
data class HazardCacheEntity(
    @PrimaryKey val locationKey: String,
    val precipitationScore: Double,
    val windScore: Double,
    val score: Double
)