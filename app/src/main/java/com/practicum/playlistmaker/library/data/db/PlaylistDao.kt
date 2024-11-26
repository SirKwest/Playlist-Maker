package com.practicum.playlistmaker.library.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlists_table ORDER BY id ASC")
    suspend fun getPlaylists() : List<PlaylistEntity>

}