package com.ubd_app.bottomMenu.driveRecord;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ubd_app.AppDatabase.DriveRecode;
import androidx.recyclerview.widget.RecyclerView.*;
import com.ubd_app.R;

import java.util.ArrayList;

public class driveRecordRecycleAdapter extends Adapter<driveRecordRecycleAdapter.RecyclerItemViewHolder> {

    private ArrayList<DriveRecode> driveRecodes = new ArrayList<>();

    private OnitemClickListener Listener = null;

    public interface OnitemClickListener {
        void onitemClick(View v, int position, DriveRecode driveRecode);
    }

    //외부에서 온클릭 리스너 설정할수있게 참조설정
    public void setOnItemClickListener(OnitemClickListener listener) {
        this.Listener = listener;
    }

    //전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return driveRecodes.size();
    }

    //외부에서 객체를 전달 받기 위한 메소드
    public void addItem(DriveRecode driveRecode) {
        driveRecodes.add(driveRecode);
    }


    //아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴 (리사이클 뷰안의 하나하나의 목록)
    @NonNull
    @Override
    public RecyclerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_drive_record_itembox, parent, false);
        return new RecyclerItemViewHolder(view);
    }

    //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(RecyclerItemViewHolder holder, int position) {
        holder.onBlind(driveRecodes.get(position));
    }

    // 아이템 뷰를 지정하는 뷰홀더 클래스.
    public class RecyclerItemViewHolder extends ViewHolder {
        private TextView startPoint;
        private TextView destination;
        private TextView totalMileage;

        public RecyclerItemViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        if (Listener != null) {
                            Listener.onitemClick(v, position, driveRecodes.get(position));
                        }
                    }
                }
            });
            startPoint = itemView.findViewById(R.id.startPoint);
            destination = itemView.findViewById(R.id.detination);
            totalMileage = itemView.findViewById(R.id.totalMileage);
        }

        //표시할 내용 설정
        void onBlind(DriveRecode driveRecode){
            startPoint.setText(driveRecode.getStartingPoint());
            destination.setText(driveRecode.getDestination());
            totalMileage.setText(String.format("%.2f Km",driveRecode.getTotalMileage()/1000));
        }
    }


}

