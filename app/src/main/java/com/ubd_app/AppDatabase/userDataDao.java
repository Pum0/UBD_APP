package com.ubd_app.AppDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface userDataDao {

    @Query("select * from userData")
    LiveData<List<userData>> select();

    @Query("select * from userData where id in (:i)")
    LiveData<List<userData>> selectUser(int i);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(userData userData);

    @Update
    void update(userData userData);

    @Delete
    void delete(userData userData);

    @Query("DELETE FROM userData")
    void deleteAll();
}
