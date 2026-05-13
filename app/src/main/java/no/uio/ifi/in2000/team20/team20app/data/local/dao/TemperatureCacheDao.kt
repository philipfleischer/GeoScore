package no.uio.ifi.in2000.team20.team20app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.team20.team20app.data.local.entity.TemperatureCacheEntity

@Dao
interface TemperatureCacheDao {

    @Query("SELECT * FROM temperature_cache WHERE stationId = :stationId")
    suspend fun getByKey(stationId: String): TemperatureCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TemperatureCacheEntity)
}