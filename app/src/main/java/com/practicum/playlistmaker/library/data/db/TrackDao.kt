package com.practicum.playlistmaker.library.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: TrackEntity)

    @Query("SELECT * FROM favorites_table ORDER BY createdAt DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT COUNT(*) > 0 FROM favorites_table WHERE trackId = :id")
    suspend fun isTrackExist(id: Int): Boolean

    @Query("DELETE FROM favorites_table WHERE trackId = :id")
    fun removeTrack(id: Int)
}