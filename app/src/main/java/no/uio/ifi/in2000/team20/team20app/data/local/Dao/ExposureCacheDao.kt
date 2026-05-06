package no.uio.ifi.in2000.team20.team20app.data.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.ExposureCacheEntity

@Dao
interface ExposureCacheDao {

    @Query("SELECT * FROM exposure_cache WHERE locationKey = :locationKey")
    suspend fun getByKey(locationKey: String): ExposureCacheEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(entity: ExposureCacheEntity)
}