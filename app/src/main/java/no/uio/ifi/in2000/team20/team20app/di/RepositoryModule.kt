package no.uio.ifi.in2000.team20.team20app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.team20.team20app.data.repository.ChatGPTRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.ChatGPTRepositoryImpl
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepositoryService
import no.uio.ifi.in2000.team20.team20app.data.repository.GeoSearchRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.GeoSearchRepositoryService
import no.uio.ifi.in2000.team20.team20app.data.repository.NveZonesRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.NveZonesRepositoryService
import no.uio.ifi.in2000.team20.team20app.data.repository.SavedRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.SavedRepositoryImpl
import no.uio.ifi.in2000.team20.team20app.data.repository.ScoreCacheRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.ScoreCacheRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFrostRepository(impl: FrostRepository): FrostRepositoryService

    @Binds
    @Singleton
    abstract fun bindGeoSearchRepository(impl: GeoSearchRepository): GeoSearchRepositoryService

    @Binds
    @Singleton
    abstract fun bindNveZonesRepository(impl: NveZonesRepository): NveZonesRepositoryService

    @Binds
    @Singleton
    abstract fun bindSavedRepository(impl: SavedRepositoryImpl): SavedRepository

    @Binds
    @Singleton
    abstract fun bindChatGPTRepository(impl: ChatGPTRepositoryImpl): ChatGPTRepository

    @Binds
    @Singleton
    abstract fun bindScoreCacheRepository(impl: ScoreCacheRepositoryImpl): ScoreCacheRepository
}
