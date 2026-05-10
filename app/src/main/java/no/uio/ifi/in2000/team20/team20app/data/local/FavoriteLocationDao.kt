package no.uio.ifi.in2000.team20.team20app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteLocationDao {

    @Query("SELECT * FROM favorite_locations ORDER BY name ASC")
    fun getAllFavorites(): Flow<List<FavoriteLocationEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_locations WHERE address = :address)")
    suspend fun isFavorite(address: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(location: FavoriteLocationEntity)

    @Query("DELETE FROM favorite_locations WHERE address = :address")
    suspend fun deleteFavoriteByAddress(address: String)
}