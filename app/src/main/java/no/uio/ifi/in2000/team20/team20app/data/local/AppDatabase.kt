package no.uio.ifi.in2000.team20.team20app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.ExposureCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.HazardCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.PrecipitationCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.ReportCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.SavedLocationDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.SnowCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.SunshineCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.TemperatureCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.TotalScoreCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.VulnerabilityCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.WindCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.ExposureCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.HazardCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.PrecipitationCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.ReportCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.SavedLocationEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.SnowCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.SunshineCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.TemperatureCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.TotalScoreCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.VulnerabilityCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.WindCacheEntity

@Database(
    entities = [
        SavedLocationEntity::class,
        HazardCacheEntity::class,
        ExposureCacheEntity::class,
        VulnerabilityCacheEntity::class,
        TotalScoreCacheEntity::class,
        TemperatureCacheEntity::class,
        WindCacheEntity::class,
        SunshineCacheEntity::class,
        SnowCacheEntity::class,
        PrecipitationCacheEntity::class,
        ReportCacheEntity::class

               ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedLocationDao(): SavedLocationDao
    abstract fun hazardCacheDao(): HazardCacheDao
    abstract fun exposureCacheDao(): ExposureCacheDao
    abstract fun vulnerabilityCacheDao(): VulnerabilityCacheDao
    abstract fun totalScoreCacheDao(): TotalScoreCacheDao
    abstract fun temperatureCacheDao(): TemperatureCacheDao
    abstract fun windCacheDao(): WindCacheDao
    abstract fun sunshineCacheDao(): SunshineCacheDao
    abstract fun snowCacheDao(): SnowCacheDao
    abstract fun precipitationCacheDao(): PrecipitationCacheDao
    abstract fun ReportCacheDao(): ReportCacheDao
}