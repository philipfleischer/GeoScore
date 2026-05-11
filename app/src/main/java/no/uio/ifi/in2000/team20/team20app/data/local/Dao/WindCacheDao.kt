package no.uio.ifi.in2000.team20.team20app.data.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.WindCacheEntity

@Dao
interface WindCacheDao {

    @Query("SELECT * FROM wind_cache WHERE stationId = :stationId")
    suspend fun getByKey(stationId: String): WindCacheEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(entity: WindCacheEntity)
}