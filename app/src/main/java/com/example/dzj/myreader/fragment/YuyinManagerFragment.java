package com.example.dzj.myreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dzj.myreader.R;

/**
 * Created by dzj on 2017/8/19.
 */

public class YuyinManagerFragment extends Fragment {
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        rootView=inflater.inflate(R.layout.yuyin_manager,null);
        return rootView;
    }
}
