package no.uio.ifi.in2000.team20.team20app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.vo.Database

@Database(
    entities = [FavoriteLocationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteLocationDao(): FavoriteLocationDao
}