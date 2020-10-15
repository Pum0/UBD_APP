package com.ubd_app.Search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.ubd_app.AppDatabase.SearchWord;
import com.ubd_app.AppDatabase.SearchWordViewModel;
import com.ubd_app.Main;
import com.ubd_app.OnBackPressedListener;
import com.ubd_app.R;

public class SearchMap extends Fragment implements OnBackPressedListener {

    private Main main;

    //레이아웃 구성
    private EditText search_text = null;
    private ImageButton backButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public SearchListAdapter searchListAdapter;

    private SearchWordViewModel wordViewModel;

    // 키패드
    private InputMethodManager imm = null;

    public boolean Type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main = (Main)getActivity();
        imm = main.getIMM();
        searchListAdapter = new SearchListAdapter(getChildFragmentManager(),2);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_list, container, false);

        wordViewModel = ((Main)getActivity()).wordViewModel;

        search_text = view.findViewById(R.id.main_search_text);
        search_text.requestFocus();

        backButton = view.findViewById(R.id.backToMap);
        tabLayout = view.findViewById(R.id.taplayout);
        tabLayout.addTab(tabLayout.newTab().setText("연관검색"));
        tabLayout.addTab(tabLayout.newTab().setText("최근검색"));
        viewPager = view.findViewById(R.id.viewpager);

        viewPager.setAdapter(searchListAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeSearchBar(v);
            }
        });
        return view;
    }

    public void closeSearchBar(View v){
        main.getIMM().hideSoftInputFromWindow(main.search.getSearch_text().getWindowToken(), 0);
        main.changeSearchBar(v);
        search_text.setText("");
    }

    public void addSearchInsert(SearchWord searchWord){
        String s = String.valueOf(search_text.getText());

        if (s!="") {
            wordViewModel.save(searchWord);
            Log.d("insert", String.valueOf(s));
        }
    }

    public EditText getSearch_text() {
        return search_text;
    }

    @Override
    public void onBackPressed() {
        backButton.performClick();
    }
}
