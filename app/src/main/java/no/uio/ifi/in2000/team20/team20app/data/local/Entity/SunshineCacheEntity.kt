package no.uio.ifi.in2000.team20.team20app.data.local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caches 1991–2020 monthly sunshine normals for a station.
// stationId: unique station identifier (e.g., SN123)
// monthlyHoursPerDay: JSON-encoded List<Double> of 12 values (Jan=index 0)
// stationName/distanceKm: metadata about the station (for UI display)
@Entity(tableName = "sunshine_cache")
data class SunshineCacheEntity(
    @PrimaryKey val stationId: String,
    val monthlyHoursPerDay: String,
    val stationName: String?,
    val distanceKm: Double?
)