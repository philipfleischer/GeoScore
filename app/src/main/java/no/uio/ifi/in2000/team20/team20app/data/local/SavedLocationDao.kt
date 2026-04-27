package no.uio.ifi.in2000.team20.team20app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLocationDao {

    @Query("SELECT * FROM favorite_locations ORDER BY name ASC")
    fun getAllSaved(): Flow<List<SavedLocationEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_locations WHERE address = :address)")
    suspend fun isSaved(address: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaved(location: SavedLocationEntity)

    @Query("DELETE FROM favorite_locations WHERE address = :address")
    suspend fun deleteSavedByAddress(address: String)
}