package com.ubd_app.AppDatabase;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.Room;

public class AppdbClient {
    private Context context;
    private static AppdbClient mAppdbclient;

    private AppDatabase appDatabase;

    private AppdbClient(Context context){
        this.context = context;
        appDatabase = Room.databaseBuilder(context, AppDatabase.class,"AppDatabase").build();
    }

    public static synchronized AppdbClient getInstance(Context context){
        if (mAppdbclient == null){
            mAppdbclient = new AppdbClient(context);
        }
        return mAppdbclient;
    }
    public AppDatabase getAppDatabase(){
        return appDatabase;
    }

}
