package no.uio.ifi.in2000.team20.team20app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caches 1991–2020 monthly temperature normals for a station.
// stationId: unique station identifier (e.g., SN123)
// monthlyMean/Max/Min: JSON-encoded List<Double> of 12 values (Jan=index 0)
@Entity(tableName = "temperature_cache")
data class TemperatureCacheEntity(
    @PrimaryKey val stationId: String,
    val monthlyMean: String,
    val monthlyMax: String,
    val monthlyMin: String
)