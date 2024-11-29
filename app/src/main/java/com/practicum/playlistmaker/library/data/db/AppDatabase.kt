package com.practicum.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class, PlaylistsTracksEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun tracksDao(): TracksDao
    abstract fun playlistsDao(): PlaylistDao
    abstract fun playlistTracksDao(): PlaylistTracksDao
}
