package com.practicum.playlistmaker.library.data.repository

import androidx.room.withTransaction
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.PlaylistDbConverter
import com.practicum.playlistmaker.library.data.db.PlaylistEntity
import com.practicum.playlistmaker.library.data.db.PlaylistsTracksEntity
import com.practicum.playlistmaker.library.data.db.TrackDbConverter
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(private val appDatabase: AppDatabase, private val converter: PlaylistDbConverter, private val trackDbConverter: TrackDbConverter) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistsDao().insertPlaylist(converter.map(playlist))
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playLists = appDatabase.playlistsDao().getPlaylists()
        emit(convertPlaylistFromDbEntity(playLists))
    }

    override suspend fun getPlaylistById(id: Int): Flow<Playlist> = flow {
        val playlist = appDatabase.playlistsDao().getPlaylistById(id)
        emit(converter.map(playlist))
    }

    override suspend fun getTracksByIds(ids: List<Int>): Flow<List<Track>> = flow {
        val tracks = appDatabase.tracksDao().getTracksByIds(ids)
        val result = tracks.map { track -> trackDbConverter.map(track) }
        emit(result)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        appDatabase.withTransaction {
            appDatabase.playlistTracksDao()
                .insertRecord(PlaylistsTracksEntity(0, playlist.id, track.trackId))
            appDatabase.tracksDao().insertTrack(trackDbConverter.map(track))
        }
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        appDatabase.playlistTracksDao().deleteTrackRecord(track.trackId, playlist.id)
    }


    override suspend fun deletePlaylistById(id: Int) {
        appDatabase.withTransaction {
            appDatabase.playlistsDao().deletePlaylistById(id)
            appDatabase.playlistTracksDao().deleteTracksByPlaylistId(id)
        }
    }

    private suspend fun convertPlaylistFromDbEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> converter.map(playlist) }
    }
}