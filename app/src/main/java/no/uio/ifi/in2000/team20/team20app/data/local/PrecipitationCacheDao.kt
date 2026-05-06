package no.uio.ifi.in2000.team20.team20app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PrecipitationCacheDao {

    @Query("SELECT * FROM precipitation_cache WHERE locationKey = :locationKey")
    suspend fun getByKey(locationKey: String): PrecipitationCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PrecipitationCacheEntity)
}
