package no.uio.ifi.in2000.team20.team20app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caches 1991–2020 monthly wind normals for a station.
// stationId: unique station identifier (e.g., SN123)
// monthlyMean/MaxSpeed/MaxGust: JSON-encoded List<Double> of 12 values (Jan=index 0)
@Entity(tableName = "wind_cache")
data class WindCacheEntity(
    @PrimaryKey val stationId: String,
    val monthlyMean: String,
    val monthlyMaxSpeed: String,
    val monthlyMaxGust: String
)