package no.uio.ifi.in2000.team20.team20app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caches 1991–2020 monthly precipitation normals for a station.
// stationId: unique station identifier (e.g., SN123)
// monthlyRainyDays: JSON-encoded List<Double> of 12 values — avg rainy days per month (>=1mm threshold)
// monthlyMaxDaily:  JSON-encoded List<Double> of 12 values — avg max single-day precipitation (mm)
@Entity(tableName = "precipitation_cache")
data class PrecipitationCacheEntity(
    @PrimaryKey val stationId: String,
    val monthlyRainyDays: String,
    val monthlyMaxDaily: String
)