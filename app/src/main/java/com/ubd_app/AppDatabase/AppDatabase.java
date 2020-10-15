package com.ubd_app.AppDatabase;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SearchWord.class,FavoriteDB.class,DriveRecode.class,userData.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    //데이터베이스를 매번 생성하는건 리소스를 많이사용하므로 싱글톤이 권장된다고한다.
    private static AppDatabase INSTANCE;

    public abstract SearchWordDao searchWordDao();
    public abstract FavoriteDBDao favoriteDBDao();
    public abstract DriveRecodeDao driveRecodeDao();
    public abstract userDataDao userDataDao();

    public static AppDatabase getAppDatabase(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class,"word-list").build();

        }
        return INSTANCE;
    }
    public static void destroyInstance(){
        INSTANCE = null;
    }
}
