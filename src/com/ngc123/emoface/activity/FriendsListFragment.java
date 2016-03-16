package com.ngc123.emoface.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ngc123.emoface.R;

public class FriendsListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);
        rootView.findViewById(R.id.friends).setEnabled(false);
        rootView.findViewById(R.id.address_book).setEnabled(true);
        return rootView;
    }

    
}
