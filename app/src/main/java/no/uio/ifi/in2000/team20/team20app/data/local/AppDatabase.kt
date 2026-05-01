package no.uio.ifi.in2000.team20.team20app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
//import androidx.room.vo.Database

@Database(
    entities = [
        SavedLocationEntity::class,
        HazardCacheEntity::class,
        ExposureCacheEntity::class,
        VulnerabilityCacheEntity::class,
        TotalScoreCacheEntity::class
               ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedLocationDao(): SavedLocationDao
    abstract fun hazardCacheDao(): HazardCacheDao
    abstract fun exposureCacheDao(): ExposureCacheDao
    abstract fun vulnerabilityCacheDao(): VulnerabilityCacheDao
    abstract fun totalScoreCacheDao(): TotalScoreCacheDao
}