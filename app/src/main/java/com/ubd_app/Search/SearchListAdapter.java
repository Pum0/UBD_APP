package com.ubd_app.Search;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.ubd_app.Search.Search_Associated.AssociatedSearchList;
import com.ubd_app.Search.Search_Recet.RecentSearchesList;

public class SearchListAdapter extends FragmentPagerAdapter {

    private int mNumOfTap;

    private AssociatedSearchList associatedSearchList;
    private RecentSearchesList recentSearchesList;

    //public SearchWord searchWord;

    public SearchListAdapter(FragmentManager fm, int NumOfTap){
        super(fm);
        mNumOfTap = NumOfTap;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case (0):
                associatedSearchList = new AssociatedSearchList();
                return associatedSearchList;
            case (1):
                recentSearchesList = new RecentSearchesList();
                return recentSearchesList;
        }
        return null;
    }

    @Override
    public int getCount() {
        return mNumOfTap;
    }
}
