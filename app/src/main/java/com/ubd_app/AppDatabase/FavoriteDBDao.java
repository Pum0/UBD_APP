package com.ubd_app.AppDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

// 모든 디비 CRUD작업은 메인스레드가아닌 백그라운드로 작업해야한다.
// (단, 라이브데이터는 반응시 자기가 알아서 백그라운드로 작업을 처리해준다. 굳)
@Dao
public interface FavoriteDBDao {

//    @Query("SELECT * FROM SearchWord")
//    LiveData<List<SearchWord>> getAll();
//    //LiveData => Todo테이블에 있는 모든 객체를 계속 관찰하고있다가 변경이 일어나면 그것을 자동으로 업데이트하도록한다.
//    //getAll() 은 관찰 가능한 객체가 된다.(즉 디비변경시 반응)


    @Query("select * from FavoriteList")
    LiveData<List<FavoriteDB>> select();

    //value) 1 : 집 , 2 : 회사 , 3이상 : 일반 즐겨찾기
    @Query("select * from FavoriteList where Value in (:Value)")
    FavoriteDB selectValue(int Value);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteDB favoriteDB);

    @Update
    void update(FavoriteDB favoriteDB);

    @Delete
    void delete(FavoriteDB favoriteDB);

    @Query("DELETE FROM FavoriteList where Value =(:Value)")
    void deleteValue (int Value);

    @Query("DELETE FROM FavoriteList")
    void deleteAll();

}
