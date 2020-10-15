package com.ubd_app.Search.Search_Associated;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.naver.maps.geometry.LatLng;
import com.ubd_app.AppDatabase.FavoriteDB;
import com.ubd_app.AppDatabase.SearchWord;
import com.ubd_app.Main;
import com.ubd_app.R;

public class AssociatedSearchList extends Fragment implements PlacesAutoCompleteAdapter.ClickListener {

    private View view;

    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private RecyclerView recyclerView;


    private String Key = "AIzaSyDW9oIThZUVQ6TObccPbg5JlhEW56Q-3NA";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_list_associated, container, false);

        Places.initialize(getContext(), Key);

        recyclerView = view.findViewById(R.id.a_recyclerView);
        ((Main) getActivity()).search.getSearch_text().addTextChangedListener(filterTextWatcher);

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAutoCompleteAdapter.setClickListener(this);
        recyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();

        return view;
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (recyclerView.getVisibility() == View.GONE) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };


    @Override
    public void click(Place place) {
        //글자가 눌렀을떄 작업
        if (((Main) getActivity()).search.Type) {
            searchPlace(place);
        } else {
            searchFavorite(place);
        }
    }

    public void searchPlace(Place place) {
        String name = place.getName();
        String address = place.getAddress();
        Double latitude = place.getLatLng().latitude;
        Double longitude = place.getLatLng().longitude;

        ((Main) getActivity()).search.addSearchInsert(new SearchWord(name, address, latitude, longitude));


       LatLng latLng = new LatLng(latitude, longitude);

        if(((Main) getActivity()).map.findRoute(latLng)) {
            ((Main) getActivity()).map.setCamera(latLng, address);
        }

        ((Main) getActivity()).search.closeSearchBar(getView());

    }

    public void searchFavorite(Place place) {

        String name = place.getName();
        String address = place.getAddress();
        Double latitude = place.getLatLng().latitude;
        Double longitude = place.getLatLng().longitude;


        ((Main) getActivity()).favoriteDBViewModel.save
                (new FavoriteDB(((Main)getActivity()).placeNumber, name
                        , address, latitude, longitude));

        ((Main) getActivity()).search.closeSearchBar(getView());


        Toast.makeText(getContext(), name + "  추가되었습니다.", Toast.LENGTH_SHORT).show();

    }
}
