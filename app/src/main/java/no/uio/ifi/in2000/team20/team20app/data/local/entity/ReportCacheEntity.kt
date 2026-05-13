package no.uio.ifi.in2000.team20.team20app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "report_cache")
data class ReportCacheEntity(
    @PrimaryKey val locationKey: String,
    val extremePrecipitationText: String?,
    val extremeWindText: String?,
    val floodText: String?,
    val landslideText: String?,
)