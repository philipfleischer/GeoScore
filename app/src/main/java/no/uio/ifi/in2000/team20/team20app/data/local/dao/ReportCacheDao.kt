package no.uio.ifi.in2000.team20.team20app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.team20.team20app.data.local.entity.ReportCacheEntity

@Dao
interface ReportCacheDao {

    @Query("SELECT * FROM report_cache WHERE locationKey = :locationKey")
    suspend fun getByKey(locationKey: String): ReportCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ReportCacheEntity)
}