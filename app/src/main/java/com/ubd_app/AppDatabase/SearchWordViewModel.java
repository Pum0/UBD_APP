package com.ubd_app.AppDatabase;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchWordViewModel extends AndroidViewModel {

    private SearchWordDao dao;
    private ExecutorService executorService;

    public SearchWordViewModel(@NonNull Application application) {
        super(application);
        dao = AppdbClient.getInstance(application).getAppDatabase().searchWordDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<SearchWord>> getAll(){
        return dao.select();
    }

    public void save(SearchWord searchWord){
        executorService.execute(()->dao.insert(searchWord));
    }

    public void delete(SearchWord searchWord){
        executorService.execute(()->dao.delete(searchWord));
    }

    public void deleteAll(){
        executorService.execute(()->dao.deleteAll());
    }
}
