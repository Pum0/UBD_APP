package com.ubd_app.Search.Search_Recet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.*;
import com.ubd_app.AppDatabase.SearchWord;
import com.ubd_app.R;

import java.util.ArrayList;

public class RecentRecyclerAdapter extends Adapter<RecentRecyclerAdapter.RecyclerItemViewHolder> {

    private ArrayList<SearchWord> searchListWords = new ArrayList<>();

    private OnitemClickListener Listener = null;

    public interface OnitemClickListener{
        void onitemClick(View v, int position, SearchWord searchWord);
    }

    public void setOnItemClickListener(OnitemClickListener listener){
        this.Listener = listener;
    }

    @Override
    public int getItemCount() {
        return searchListWords.size();
    }

    public void addItem(SearchWord searchWord){
        searchListWords.add(searchWord);
    }

    @NonNull
    @Override
    public RecyclerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_recent_word_box,parent,false);
        return new RecyclerItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerItemViewHolder holder, int position) {
        holder.onBlind(searchListWords.get(position));
    }



    public class RecyclerItemViewHolder extends ViewHolder {

        private TextView textArea;

        private TextView textAddress;


        public RecyclerItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (position !=RecyclerView.NO_POSITION){
                        if (Listener !=null){
                            Listener.onitemClick(v,position, searchListWords.get(position));
                        }
                    }
                }
            });

            textArea = itemView.findViewById(R.id.itemArea);
            textAddress = itemView.findViewById(R.id.itemAddress);
        }

        void onBlind(SearchWord SLW){
            textArea.setText(SLW.getName());
            textAddress.setText(SLW.getAddress());
        }

    }

}




