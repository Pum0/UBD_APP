package com.ubd_app.AppDatabase;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DriveRecordeViewModel extends AndroidViewModel {

    private DriveRecodeDao dao;
    private ExecutorService executorService;

    public DriveRecordeViewModel(@NonNull Application application) {
        super(application);
        dao = AppdbClient.getInstance(application).getAppDatabase().driveRecodeDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<DriveRecode>> getAll(){
        return dao.select();
    }

    public void save(DriveRecode driveRecode){
        executorService.execute(()->dao.insert(driveRecode));
    }

    public void delete(DriveRecode driveRecode){
        executorService.execute(()->dao.delete(driveRecode));
    }

    public void deleteAll(){
        executorService.execute(()->dao.deleteAll());
    }
}
