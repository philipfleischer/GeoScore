package no.uio.ifi.in2000.team20.team20app.data.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.SavedLocationEntity

@Dao
interface SavedLocationDao {

    @Query("SELECT * FROM saved_locations ORDER BY name ASC")
    fun getAllSaved(): Flow<List<SavedLocationEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_locations WHERE address = :address)")
    suspend fun isSaved(address: String): Boolean

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertSaved(location: SavedLocationEntity)

    @Query("DELETE FROM saved_locations WHERE address = :address")
    suspend fun deleteSavedByAddress(address: String)
}