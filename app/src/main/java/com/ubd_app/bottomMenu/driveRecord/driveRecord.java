package com.ubd_app.bottomMenu.driveRecord;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.naver.maps.geometry.LatLng;
import com.ubd_app.AppDatabase.DriveRecode;
import com.ubd_app.AppDatabase.DriveRecordeViewModel;
import com.ubd_app.Main;
import com.ubd_app.R;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class driveRecord extends BottomSheetDialogFragment {

    private driveRecord driveRecordThis;

    private Main main;

    private View driveView;

    private RecyclerView recyclerView;

    private driveRecordRecycleAdapter recyclerAdapter;

    private DriveRecordeViewModel driveRecordeViewModel;

    public driveRecord() {}

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.driveRecordThis = this;
        this.main = (Main)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        driveView = inflater.inflate(R.layout.bottom_drive_record, container, false);

        driveRecordeViewModel = main.driveRecordeViewModel;

        recyclerView = driveView.findViewById(R.id.record_recycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerAdapter = new driveRecordRecycleAdapter();
        recyclerAdapter.setOnItemClickListener(new driveRecordRecycleAdapter.OnitemClickListener() {
            @Override
            public void onitemClick(View v, int position, DriveRecode driveRecode) {
                //누르면 동작할 내용을 적어줌
                main.getSupportFragmentManager().beginTransaction().replace(R.id.main_recodeFrame, new recordFragment(driveRecode)).commitAllowingStateLoss();
                main.getSupportFragmentManager().beginTransaction().remove(driveRecordThis);
                getDialog().hide();
            }
        });
        recyclerView.setAdapter(recyclerAdapter);

        setDriveRecordeList();

        return driveView;
    }

    private void setDriveRecordeList() {

        driveRecordeViewModel.getAll().observe(this, new Observer<List<DriveRecode>>() {
            @Override
            public void onChanged(List<DriveRecode> driveRecodes) {
                for (int i = 0; i < driveRecodes.size(); i++) {
                    DriveRecode dr = driveRecodes.get(i);

                    //저장할 데이터 지정해서 삽입
                    recyclerAdapter.addItem(dr);
                }
                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }

}

