package com.practicum.playlistmaker.library.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface TracksDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: TrackEntity)

    @Query("SELECT * FROM tracks WHERE trackId IN (:ids)")
    suspend fun getTracksByIds(ids: List<Int>) : List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE isFavorite = 1 ORDER BY createdAt DESC")
    suspend fun getFavorites(): List<TrackEntity>

    @Query("SELECT COUNT(*) > 0 FROM tracks WHERE trackId = :id")
    suspend fun isTrackExist(id: Int): Boolean

    @Query("DELETE FROM tracks WHERE trackId = :id")
    fun removeTrack(id: Int)

    @Query("UPDATE tracks SET isFavorite = 0 WHERE trackId = :id")
    fun removeFromFavorites(id: Int)
}