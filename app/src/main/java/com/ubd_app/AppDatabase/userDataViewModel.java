package com.ubd_app.AppDatabase;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class userDataViewModel extends AndroidViewModel {
    private userDataDao dao;
    private ExecutorService executorService;

    public userDataViewModel(@NonNull Application application) {
        super(application);
        dao = AppdbClient.getInstance(application).getAppDatabase().userDataDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<userData>> getAll() {
        return dao.select();
    }

    public LiveData<List<userData>> getUser(int i) {
        return dao.selectUser(i);
    }

    public void save(userData userData) {
        executorService.execute(() -> dao.insert(userData));
    }

    public void update(userData userData) {
        executorService.execute(() -> dao.update(userData)); }

    public void delete(userData userData) {
        executorService.execute(() -> dao.delete(userData));
    }

    public void deleteAll() {
        executorService.execute(() -> dao.deleteAll());
    }
}
