package it.ilker.repeatcard.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import it.ilker.repeatcard.db.directory.Directory
import it.ilker.repeatcard.db.directory.FlashcardDirectoryDao
import it.ilker.repeatcard.db.flashcard.Flashcard
import it.ilker.repeatcard.db.flashcard.FlashcardDao
import it.ilker.repeatcard.db.notification.Notification
import it.ilker.repeatcard.db.notification.NotificationDao

@Database(
    entities = [Flashcard::class, Directory::class, Notification::class],
    version = 2,
    exportSchema = false
)
abstract class FlashcardDatabase : RoomDatabase() {

    abstract fun flashcardDao(): FlashcardDao
    abstract fun directoryDao(): FlashcardDirectoryDao
    abstract fun notificationsDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: FlashcardDatabase? = null

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                        CREATE TABLE flashcard_directories_table (
                            id INTEGER PRIMARY KEY NOT NULL,
                            title TEXT NOT NULL,
                            creationDate TEXT
                        )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): FlashcardDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlashcardDatabase::class.java,
                    "flashcard_table"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
