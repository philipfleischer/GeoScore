package no.uio.ifi.in2000.team20.team20app.data.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.TemperatureCacheEntity

@Dao
interface TemperatureCacheDao {

    @Query("SELECT * FROM temperature_cache WHERE stationId = :stationId")
    suspend fun getByKey(stationId: String): TemperatureCacheEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(entity: TemperatureCacheEntity)
}