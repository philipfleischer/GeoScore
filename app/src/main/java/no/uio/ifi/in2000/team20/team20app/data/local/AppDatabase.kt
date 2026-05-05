package no.uio.ifi.in2000.team20.team20app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

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
        PrecipitationCacheEntity::class
               ],
    version = 5,
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
}