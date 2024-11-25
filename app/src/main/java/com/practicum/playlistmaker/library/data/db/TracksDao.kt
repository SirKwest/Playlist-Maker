package com.practicum.playlistmaker.library.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TracksDao {
    @Query("SELECT trackId FROM playlist_tracks_table WHERE playlistId = :playlistId")
    suspend fun getAllTrackIdsByPlaylistId(playlistId: Int) : List<Int>

    @Query("SELECT playlistId FROM playlist_tracks_table WHERE trackId = :trackId")
    suspend fun getAllPlaylistsByTrackId(trackId: Int) : List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(playlistsTracksEntity: PlaylistsTracksEntity)

    @Query("DELETE FROM playlist_tracks_table WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrackRecord(trackId: Int, playlistId: Int)
}