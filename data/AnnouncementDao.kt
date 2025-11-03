package data

import androidx.room.*
import model.Announcement

@Dao
interface AnnouncementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)

    @Delete
    suspend fun deleteAnnouncement(announcement: Announcement)

    @Query("SELECT * FROM announcements ORDER BY createdDate DESC")
    suspend fun getAllAnnouncements(): List<Announcement>
}
