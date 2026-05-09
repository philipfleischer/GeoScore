package no.uio.ifi.in2000.team20.team20app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.team20.team20app.data.local.AppDatabase
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.ExposureCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.HazardCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.PrecipitationCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.ReportCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.SavedLocationDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.SnowCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.SunshineCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.TemperatureCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.TotalScoreCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.VulnerabilityCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.WindCacheDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "team20_app_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideSavedLocationDao(db: AppDatabase): SavedLocationDao = db.savedLocationDao()

    @Provides
    @Singleton
    fun provideTemperatureCacheDao(db: AppDatabase): TemperatureCacheDao = db.temperatureCacheDao()

    @Provides
    @Singleton
    fun provideWindCacheDao(db: AppDatabase): WindCacheDao = db.windCacheDao()

    @Provides
    @Singleton
    fun provideSunshineCacheDao(db: AppDatabase): SunshineCacheDao = db.sunshineCacheDao()

    @Provides
    @Singleton
    fun provideSnowCacheDao(db: AppDatabase): SnowCacheDao = db.snowCacheDao()

    @Provides
    @Singleton
    fun providePrecipitationCacheDao(db: AppDatabase): PrecipitationCacheDao = db.precipitationCacheDao()

    @Provides
    @Singleton
    fun provideHazardCacheDao(db: AppDatabase): HazardCacheDao = db.hazardCacheDao()

    @Provides
    @Singleton
    fun provideExposureCacheDao(db: AppDatabase): ExposureCacheDao = db.exposureCacheDao()

    @Provides
    @Singleton
    fun provideVulnerabilityCacheDao(db: AppDatabase): VulnerabilityCacheDao = db.vulnerabilityCacheDao()

    @Provides
    @Singleton
    fun provideTotalScoreCacheDao(db: AppDatabase): TotalScoreCacheDao = db.totalScoreCacheDao()

    @Provides
    @Singleton
    fun provideReportCacheDao(db: AppDatabase): ReportCacheDao = db.ReportCacheDao()
}
