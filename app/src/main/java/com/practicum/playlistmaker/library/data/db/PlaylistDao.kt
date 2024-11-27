package com.practicum.playlistmaker.library.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.domain.models.Playlist

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlists_table ORDER BY id ASC")
    suspend fun getPlaylists() : List<PlaylistEntity>

    @Query("SELECT * FROM playlists_table WHERE id = :id")
    suspend fun getPlaylistById(id: Int) : PlaylistEntity

    @Query("DELETE FROM playlists_table WHERE id = :id")
    suspend fun deletePlaylistById(id: Int)
}