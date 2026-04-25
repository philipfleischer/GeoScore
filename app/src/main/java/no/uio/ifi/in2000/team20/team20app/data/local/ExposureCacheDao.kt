package no.uio.ifi.in2000.team20.team20app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExposureCacheDao {

    @Query("SELECT * FROM exposure_cache WHERE locationKey = :locationKey")
    suspend fun getByKey(locationKey: String): ExposureCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ExposureCacheEntity)
}