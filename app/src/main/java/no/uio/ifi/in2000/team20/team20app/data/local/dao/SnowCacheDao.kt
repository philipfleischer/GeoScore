package no.uio.ifi.in2000.team20.team20app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.team20.team20app.data.local.entity.SnowCacheEntity

@Dao
interface SnowCacheDao {

    @Query("SELECT * FROM snow_cache WHERE stationId = :stationId")
    suspend fun getByKey(stationId: String): SnowCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SnowCacheEntity)
}