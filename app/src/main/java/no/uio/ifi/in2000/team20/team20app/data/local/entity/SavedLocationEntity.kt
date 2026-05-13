package no.uio.ifi.in2000.team20.team20app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_locations")
data class SavedLocationEntity(
    @PrimaryKey
    val address: String,
    val name: String,
    val municipality: String?,
    val county: String?,
    val lat: Double,
    val lon: Double,
    val savedAt: Long = 0L
)