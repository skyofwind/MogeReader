package com.example.dzj.myreader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dzj.myreader.R;
import com.example.dzj.myreader.activity.AddXiaoshuoActivity;
import com.example.dzj.myreader.adpter.FictionListAdapter;
import com.example.dzj.myreader.modle.TxtFile;
import com.example.dzj.myreader.utils.FileListUtil;
import com.example.dzj.myreader.utils.ThreadUtil;
import com.example.dzj.myreader.view.FunnyView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dzj on 2017/8/21.
 */

public class SacnManagerFragment extends Fragment {
    private static final String TAG="SacnManagerFragment";
    private static final long DAY=86400000;
    private static final long WEEK=DAY*7;
    private static final long MONTH=DAY*30;
    private View rootView;
    private FunnyView funnyView;
    private ExpandableListView listView;
    private FictionListAdapter fictionListAdapter;
    private LinearLayout linearLayout;
    private List<TxtFile> txtFiles;
    private List<TxtFile> day,week,month,other;
    private List<String> group;
    private List<List<TxtFile>> files;

    private final Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case  0x10:
                    initView();
                    break;
                case 0x11:
                    removeFunnyView();
                    //((AddXiaoshuoActivity)getContext()).setIsfunnyCompleted(true);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView=inflater.inflate(R.layout.scan_manager,null);

        linearLayout=(LinearLayout)rootView.findViewById(R.id.scan_root);
        funnyView=(FunnyView)rootView.findViewById(R.id.funny);
        funnyView.start();
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                seachTxt();
                sort();
                initDatas();
            }
        });
        return rootView;
    }
    private void removeFunnyView(){
        funnyView.stop();
        linearLayout.removeView(funnyView);
    }
    private void initView(){
        listView = (ExpandableListView)rootView.findViewById(R.id.expandingListView);
        group = new ArrayList<>();
        files = new ArrayList<>();
        prepareData();
        fictionListAdapter = new FictionListAdapter(getContext(), group, files);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });
        listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return true;
            }
        });
        listView.setAdapter(fictionListAdapter);
        for(int i = 0; i < group.size(); i++){
            listView.expandGroup(i);
        }

    }
    private void prepareData() {
        if(day.size() > 0){
            group.add("一天之内");
            files.add(day);
        }
        if(week.size() > 0){
            group.add("一周之内");
            files.add(week);
        }
        if(month.size() > 0){
            group.add("一月之内");
            files.add(month);
        }
        if(other.size() > 0){
            group.add("一月之前");
            files.add(other);
        }
    }
    private void seachTxt(){
        txtFiles=new ArrayList<>();
        String internal= FileListUtil.getStoragePath(getContext(),true);
        String sdcard=FileListUtil.getStoragePath(getContext(),false);
        getAllTxt(internal);
        getAllTxt(sdcard);
        //Log.i("seach_length",internal+" "+sdcard);

    }
    private void sort(){
        Collections.sort(txtFiles,new Comparator<TxtFile>(){
            @Override
            public int compare(TxtFile o1, TxtFile o2) {
                int i=mCompare(o1.getLastModified(),o2.getLastModified());
                return i;
            }
        });
        Collections.reverse(txtFiles);
    }
    private void initDatas(){
        day=new ArrayList<>();
        week=new ArrayList<>();
        month=new ArrayList<>();
        other=new ArrayList<>();
        long s=System.currentTimeMillis();
        for(TxtFile t:txtFiles){
            long time=s-t.getLastModified();
            if(time<=DAY){
                day.add(t);
            }else if (time>DAY&&time<=WEEK){
                week.add(t);
            }else if (time>WEEK&&time<=MONTH){
                month.add(t);
            }else {
                other.add(t);
            }
        }
        mHandler.sendEmptyMessage(0x11);
        txtFiles.clear();
        mHandler.sendEmptyMessage(0x10);
    }
    private void getAllTxt(String path){
        if(null != path){
            File file=new File(path);
            if(!file.exists()){
                return;
            }
            File[] files=file.listFiles();
            if(files==null){
                return;
            }
            for (File f:files){
                if(f.isDirectory()){
                    getAllTxt(f.getPath());
                }else{
                    if(f.getName().length()>=5){
                        String suffix=f.getName().substring(f.getName().length()-4,f.getName().length());
                        if(suffix.equals(".txt")){
                            TxtFile txtFile=new TxtFile(f.getName(),f.getPath(),f.lastModified(),getFileSize(f.length()), true);
                            txtFiles.add(txtFile);
                        }
                    }
                }
            }
        }

    }

    private String getFileSize(long length){
        String result=null;
        long size=0;
        long kb=1024;
        long mb=kb*1024;
        if(length>=1024){
            if(length>=mb){
                size=length/mb;
                result=size+"mb";
            }else {
                size=length/kb;
                result=size+"kb";
            }

        }else {
            size=length;
            result=size+"b";
        }
        return result;
    }
    public static int mCompare(long x,long y){
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
    public void chooseAll(){
        fictionListAdapter.chooseAll();
    }
    public List<TxtFile> getAdd(){
        return fictionListAdapter.getAdd();
    }
}
