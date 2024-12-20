package com.practicum.playlistmaker.library.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTracksDao {
    @Query("SELECT trackId FROM playlist_tracks_table WHERE playlistId = :playlistId ORDER BY id DESC")
    suspend fun getAllTrackIdsByPlaylistId(playlistId: Int) : List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(playlistsTracksEntity: PlaylistsTracksEntity)

    @Query("SELECT playlistId FROM playlist_tracks_table WHERE trackId = :trackId")
    suspend fun getPlaylistIdsByTrackId(trackId: Int) : List<Int>

    @Query("DELETE FROM playlist_tracks_table WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrackRecord(trackId: Int, playlistId: Int)

    @Query("DELETE FROM playlist_tracks_table WHERE playlistId = :playlistId")
    fun deleteTracksByPlaylistId(playlistId: Int)
}