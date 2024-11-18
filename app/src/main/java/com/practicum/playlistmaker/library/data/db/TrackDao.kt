package com.practicum.playlistmaker.library.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: TrackEntity)

    @Query("SELECT * FROM favorites_table")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT 1 FROM favorites_table WHERE trackId = :id")
    suspend fun isTrackExist(id: Int): Int?

    @Query("DELETE FROM favorites_table WHERE trackId = :id")
    fun removeTrack(id: Int)
}