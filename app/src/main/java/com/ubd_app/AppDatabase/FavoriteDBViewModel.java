package com.ubd_app.AppDatabase;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteDBViewModel extends AndroidViewModel {

    private FavoriteDBDao dao;
    private ExecutorService executorService;

    public FavoriteDBViewModel(@NonNull Application application) {
        super(application);
        dao = AppdbClient.getInstance(application).getAppDatabase().favoriteDBDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<FavoriteDB>> getAll(){
        return dao.select();
    }

    //place) 0 : 집 , 1 : 회사 , 2이상 : 일반 즐겨찾기
    public FavoriteDB getValue(int value) {return dao.selectValue(value);}

    public void save(FavoriteDB favoriteDB){
        executorService.execute(()->dao.insert(favoriteDB));
    }

    public void delete(FavoriteDB favoriteDB){
        executorService.execute(()->dao.delete(favoriteDB));
    }

    public void deleteValue(int Value){
        executorService.execute(()->dao.deleteValue(Value));
    }

    public void deleteAll() {executorService.execute(()->dao.deleteAll());}

}
