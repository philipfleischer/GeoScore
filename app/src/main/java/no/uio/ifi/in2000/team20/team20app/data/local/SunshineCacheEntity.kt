package no.uio.ifi.in2000.team20.team20app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caches 1991–2020 monthly sunshine normals for a geographic area.
// locationKey format: "%.2f, %.2f".format(lat, lon) (~1km precision)
// monthlyHoursPerDay: JSON-encoded List<Double> of 12 values (Jan=index 0)
// stationName/distanceKm: metadata for the nearest station that has sunshine data
//   (only ~36 stations in Norway measure sunshine, so the nearest may be far away)
@Entity(tableName = "sunshine_cache")
data class SunshineCacheEntity(
    @PrimaryKey val locationKey: String,
    val monthlyHoursPerDay: String,
    val stationName: String?,
    val distanceKm: Double?
)
