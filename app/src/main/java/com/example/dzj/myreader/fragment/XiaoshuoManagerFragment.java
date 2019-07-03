package com.example.dzj.myreader.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dzj.myreader.R;
import com.example.dzj.myreader.activity.AddXiaoshuoActivity;
import com.example.dzj.myreader.activity.MainActivity;
import com.example.dzj.myreader.adpter.XiaoshuoManagerAdapter;
import com.example.dzj.myreader.broadcastreceiver.FictionUpdateReceiver;
import com.example.dzj.myreader.database.FictionDao;
import com.example.dzj.myreader.modle.TxtFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dzj on 2017/8/19.
 */

public class XiaoshuoManagerFragment extends Fragment {
    private GridView gridView;
    private List<TxtFile> files;
    private XiaoshuoManagerAdapter adapter;
    private int width;
    private FictionUpdateReceiver fictionUpdateReceiver;
    private boolean isUpdate = false;
    private LinearLayout mBottomBar;
    private TextView delete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.xiaoshuo_manager, container, false);
        initView(rootView);
        initData();
        setAdapter();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        register();
        if (isUpdate) {
            update();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegister();
    }

    private void initView(View rootView) {
        gridView = (GridView) rootView.findViewById(R.id.grid_xiaoshuo);
        mBottomBar = (LinearLayout) rootView.findViewById(R.id.mBottomBar);
        delete = (TextView) rootView.findViewById(R.id.delete);

        delete.setOnClickListener(deleteListener);
    }

    private void update() {
        Log.d("update", "我更新GridView了");
        initData();
        setAdapter();
        isUpdate = false;
    }

    private void register() {
        if (fictionUpdateReceiver == null) {
            fictionUpdateReceiver = new FictionUpdateReceiver();
            fictionUpdateReceiver.setFictionUpdateListener(new FictionUpdateReceiver.FictionUpdateListener() {
                @Override
                public void setFictionUpdateDisable() {
                    isUpdate = true;
                }
            });
            fictionUpdateReceiver.setFicitionBarChangeListenner(new FictionUpdateReceiver.FictionUpdateListener() {
                @Override
                public void setFictionUpdateDisable() {

                }
            });
            fictionUpdateReceiver.setGridLongPressListener(new FictionUpdateReceiver.FictionUpdateListener() {
                @Override
                public void setFictionUpdateDisable() {
                    MainActivity activity = (MainActivity) getActivity();
                    if (activity.isBarGone()) {
                        changeBottomBar();
                        activity.topBarChange();
                    }
                }
            });
            fictionUpdateReceiver.setToastListener(new FictionUpdateReceiver.ToastListener() {
                @Override
                public void setToast(String s) {
                    Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                }
            });
            IntentFilter filter = new IntentFilter();
            filter.addAction(FictionUpdateReceiver.FICTION_UPDATE);
            filter.addAction(FictionUpdateReceiver.FICYION_BAR_CHANGE);
            filter.addAction(FictionUpdateReceiver.GRID_LONG_PRESS);
            filter.addAction(FictionUpdateReceiver.TOAST_RECEIVER);
            getActivity().registerReceiver(fictionUpdateReceiver, filter);
        }
    }

    private void unRegister() {
        if (fictionUpdateReceiver != null) {
            getActivity().unregisterReceiver(fictionUpdateReceiver);
            fictionUpdateReceiver = null;
        }
    }

    private void initData() {
        width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        files = FictionDao.getInstance(getContext()).getAllData();
        if (files == null) {
            files = new ArrayList<>();
        }

    }

    private void setAdapter() {
        adapter = new XiaoshuoManagerAdapter(getContext(), files, width);
        adapter.setAddBookClickListener(addBookClickListener);
        gridView.setAdapter(adapter);
    }

    XiaoshuoManagerAdapter.AddBookClickListener addBookClickListener = new XiaoshuoManagerAdapter.AddBookClickListener() {
        @Override
        public void onClick() {
            Intent intent = new Intent(getActivity(), AddXiaoshuoActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener deleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (adapter != null) {
                adapter.deleteFiction();
            }
        }
    };

    public void changeBottomBar() {
        if (mBottomBar != null) {
            if (mBottomBar.getVisibility() == View.GONE) {
                mBottomBar.setVisibility(View.VISIBLE);
            } else {
                mBottomBar.setVisibility(View.GONE);
            }
        }
    }

    public void updateAdapterView() {
        if (adapter != null) {
            adapter.updateView();
        }
    }

    public void resetIsChoose(boolean a) {
        if (adapter != null) {
            adapter.resetIsChoose(a);
        }
    }

}
