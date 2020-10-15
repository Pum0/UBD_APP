package com.ubd_app.bottomMenu.favorite;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.naver.maps.geometry.LatLng;
import com.ubd_app.AppDatabase.FavoriteDB;
import com.ubd_app.AppDatabase.FavoriteDBViewModel;
import com.ubd_app.Main;
import com.ubd_app.R;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class favorites extends BottomSheetDialogFragment implements View.OnClickListener, View.OnLongClickListener {

    private Main main;

    private View favoritesView;

    public List<FavoriteDB> favoriteDBs = new ArrayList<>(8);
    public List<TextView> textViews = new ArrayList<>(8);

    //즐겨찾기 내부 버튼
    private TextView House, Business, favorite1, favorite2, favorite3, favorite4, favorite5, favorite6;

    private FavoriteDBViewModel favoriteDBViewModel;

    public favorites() {
    }


    @Override
    public void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main = (Main)getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        favoritesView = inflater.inflate(R.layout.bottom_favorites, container, false);

        favoriteDBViewModel = main.favoriteDBViewModel;

        House = (TextView) favoritesView.findViewById(R.id.favoriteHouse);
        textViews.add(House);
        Business = (TextView) favoritesView.findViewById(R.id.favoriteBisiness);
        textViews.add(Business);
        favorite1 = (TextView) favoritesView.findViewById(R.id.favoriteButton1);
        textViews.add(favorite1);
        favorite2 = (TextView) favoritesView.findViewById(R.id.favoriteButton2);
        textViews.add(favorite2);
        favorite3 = (TextView) favoritesView.findViewById(R.id.favoriteButton3);
        textViews.add(favorite3);
        favorite4 = (TextView) favoritesView.findViewById(R.id.favoriteButton4);
        textViews.add(favorite4);
        favorite5 = (TextView) favoritesView.findViewById(R.id.favoriteButton5);
        textViews.add(favorite5);
        favorite6 = (TextView) favoritesView.findViewById(R.id.favoriteButton6);
        textViews.add(favorite6);


        setButton();

        for (int i = 0; i < 8; i++) {
            textViews.get(i).setOnClickListener(this);
            textViews.get(i).setOnLongClickListener(this);
            textViews.get(i).setSelected(true);
        }


        return favoritesView;
    }

    private void setButton() {
        favoriteDBViewModel.getAll().observe(this, new Observer<List<FavoriteDB>>() {
            @Override
            public void onChanged(List<FavoriteDB> favoriteDBS) {
                for (int i = 0; i < favoriteDBS.size(); i++) {
                    setTextAndList(favoriteDBS.get(i));
                }
            }
        });
    }


    public void setTextAndList(FavoriteDB f) {
        boolean t = false;
        if (favoriteDBs.size() == 0) {
            favoriteDBs.add(f);
            textViews.get(f.getValue()).setText(f.getPlace());
        } else {
            for (int j = 0; j < favoriteDBs.size(); j++) {
                if (favoriteDBs.get(j).getValue() != f.getValue()) {
                    t = true;
                } else {
                    t = false;
                }
            }
            if (t) {
                favoriteDBs.add(f);
                textViews.get(f.getValue()).setText(f.getPlace());
            }
        }
    }


    public void favoriteSearchCheck(int i) {
        String s = textViews.get(i).getText().toString();
        if (s.equals("")) {
            main.placeNumber = i;
            getDialog().hide();
            ((Main) getActivity()).search.Type = false;
            getFragmentManager().beginTransaction().replace(R.id.frame_layout, ((Main) getActivity()).search).commitAllowingStateLoss();
        } else {
            main.placeNumber = i;
            FavoriteDB f = null;
            for (int j = 0; j < favoriteDBs.size(); j++) {
                if (favoriteDBs.get(i).getValue() == i) {
                    f = favoriteDBs.get(i);
                }
            }
            if(main.map.

                    findRoute(new LatLng(f.getLatitude(), f.getLongitude()))) {
                main.map.setCamera(new LatLng(f.getLatitude(), f.getLongitude()), f.getAddress());
            }
            getDialog().dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.favoriteHouse):
                favoriteSearchCheck(0);
                break;
            case (R.id.favoriteBisiness):
                favoriteSearchCheck(1);
                break;
            case (R.id.favoriteButton1):
                favoriteSearchCheck(2);
                break;
            case (R.id.favoriteButton2):
                favoriteSearchCheck(3);
                break;
            case (R.id.favoriteButton3):
                favoriteSearchCheck(4);
                break;
            case (R.id.favoriteButton4):
                favoriteSearchCheck(5);
                break;
            case (R.id.favoriteButton5):
                favoriteSearchCheck(6);
                break;
            case (R.id.favoriteButton6):
                favoriteSearchCheck(7);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case (R.id.favoriteHouse):
                deleteFavorite(0);
                break;
            case (R.id.favoriteBisiness):
                deleteFavorite(1);
                break;
            case (R.id.favoriteButton1):
                deleteFavorite(2);
                break;
            case (R.id.favoriteButton2):
                break;
            case (R.id.favoriteButton3):
                break;
            case (R.id.favoriteButton4):
                break;
            case (R.id.favoriteButton5):
                break;
            case (R.id.favoriteButton6):
                break;
        }

        return true;
    }

    public void deleteFavorite(int i) {
        if ((textViews.get(i).getText()).equals("")) {
            favoriteDBViewModel.deleteAll();
        } else {
            for (int j = 0; j < favoriteDBs.size(); j++) {
                if ((favoriteDBs.get(j).getPlace()).equals(textViews.get(i).getText())) {
                    favoriteDBs.remove(j);
                    favoriteDBViewModel.deleteValue(j);
                }
            }
            textViews.get(i).setText("");
        }
    }
}
