package me.saeha.android.chatproject.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.saeha.android.chatproject.model.ChattingRoom
import me.saeha.android.chatproject.model.Message


@Database(
    entities = [ChattingRoom::class, Message::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun appDao(): AppDAO

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback()


    companion object {
        private var INSTANCE: AppDataBase? = null

        fun getInstance(
            context: Context,
            scope: CoroutineScope
        ): AppDataBase? { // userData 미리 넣는걸로 인해서 scope 추가
            if (INSTANCE == null) {
                synchronized(AppDataBase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java, "user.db"
                    ).allowMainThreadQueries()
                        .addCallback(WordDatabaseCallback(scope)) // userData 미리 넣는걸로 추가한 줄
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
