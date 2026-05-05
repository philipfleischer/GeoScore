package no.uio.ifi.in2000.team20.team20app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caches 1991–2020 monthly precipitation normals for a geographic area.
// locationKey format: "%.2f, %.2f".format(lat, lon) (~1km precision)
// monthlyRainyDays: JSON-encoded List<Double> of 12 values — avg rainy days per month (>=1mm threshold)
// monthlyMaxDaily:  JSON-encoded List<Double> of 12 values — avg max single-day precipitation (mm)
@Entity(tableName = "precipitation_cache")
data class PrecipitationCacheEntity(
    @PrimaryKey val locationKey: String,
    val monthlyRainyDays: String,
    val monthlyMaxDaily: String
)
