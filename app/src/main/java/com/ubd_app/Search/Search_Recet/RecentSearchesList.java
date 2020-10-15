package com.ubd_app.Search.Search_Recet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.naver.maps.geometry.LatLng;
import com.ubd_app.AppDatabase.FavoriteDB;
import com.ubd_app.AppDatabase.SearchWord;
import com.ubd_app.AppDatabase.SearchWordViewModel;
import com.ubd_app.Main;
import com.ubd_app.R;

import java.util.List;

public class RecentSearchesList extends Fragment {

    private Main main;
    private View view;
    private RecyclerView recyclerView;
    private RecentRecyclerAdapter recyclerAdapter;
    private SearchWordViewModel wordViewModel;

    private List<SearchWord> list;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (Main)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_list_recent, container, false);

        recyclerView = view.findViewById(R.id.r_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerAdapter = new RecentRecyclerAdapter();
        recyclerAdapter.setOnItemClickListener(new RecentRecyclerAdapter.OnitemClickListener() {
            @Override
            public void onitemClick(View v, int position, SearchWord searchWord) {
                //클릭시 작업
                if (main.search.Type) {
                    searchPlace(searchWord);
                }else {
                    searchFavorite(searchWord);
                }

            }
        });
        recyclerView.setAdapter(recyclerAdapter);
        wordViewModel = main.wordViewModel;

        getSearchWordList();

        return view;
    }

    private void searchPlace(SearchWord searchWord){
        LatLng latLng = new LatLng(searchWord.getLatitude(), searchWord.getLongitude());

        if (main.map.findRoute(latLng))
            main.map.setCamera(latLng, searchWord.getAddress());
        main.search.closeSearchBar(getView());

    }
    private void searchFavorite(SearchWord searchWord){

        main.favoriteDBViewModel.save
                (new FavoriteDB(main.placeNumber,searchWord.getName()
                        ,searchWord.getAddress(),searchWord.getLatitude(),searchWord.getLongitude()));
        main.search.closeSearchBar(getView());
    }

    private void getSearchWordList() {

        wordViewModel.getAll().observe(getViewLifecycleOwner(), new Observer<List<SearchWord>>() {
            @Override
            public void onChanged(List<SearchWord> searchWords) {

                list = searchWords;

                int size = list.size() - 1;

                for (int i = size; i >= 0; i--) {

                    String name = list.get(i).getName();
                    String address = list.get(i).getAddress();
                    Double latitude = list.get(i).getLatitude();
                    Double longitude = list.get(i).getLongitude();

                    SearchWord s = new SearchWord(name,address,latitude,longitude);

                    recyclerAdapter.addItem(s);

                }
                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }

}
