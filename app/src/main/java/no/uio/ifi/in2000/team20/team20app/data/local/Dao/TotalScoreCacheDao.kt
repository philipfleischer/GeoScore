package no.uio.ifi.in2000.team20.team20app.data.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.TotalScoreCacheEntity

@Dao
interface TotalScoreCacheDao {

    @Query("SELECT * FROM total_score_cache WHERE locationKey = :locationKey")
    suspend fun getByKey(locationKey: String): TotalScoreCacheEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(entity: TotalScoreCacheEntity)
}