package com.example.dzj.myreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.dzj.myreader.R;
import com.example.dzj.myreader.activity.AddXiaoshuoActivity;
import com.example.dzj.myreader.adpter.FileListviewAdapter;
import com.example.dzj.myreader.adpter.HRecyclerviewAdapter;
import com.example.dzj.myreader.modle.TxtFile;
import com.example.dzj.myreader.utils.FileListUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dzj on 2017/8/21.
 */

public class FileManagerFragment extends Fragment implements AddXiaoshuoActivity.FragmentBackListener {

    private static final String TAG="FileManagerFragment";
    private static final String ROOT_PATH="根目录";
    private ListView listView;
    private RecyclerView recyclerView;
    private List<TxtFile> fileDatases;
    private List<String> datas;
    private List<String> skipPaths;

    private FileListviewAdapter fileListviewAdapter;
    private HRecyclerviewAdapter recyclerviewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView=inflater.inflate(R.layout.file_manager,null);
        listView=(ListView)rootView.findViewById(R.id.file_list_view);
        recyclerView=(RecyclerView)rootView.findViewById(R.id.recycle);

        initData();
        initView();

        return rootView;
    }
    private void initData(){
        fileDatases=new ArrayList<>();
        datas=new ArrayList<>();
        skipPaths=new ArrayList<>();
        FirstInit();
        datas.add(ROOT_PATH);
        skipPaths.add(ROOT_PATH);

    }
    private void FirstInit(){
        String internal= FileListUtil.getStoragePath(getContext(),true);
        String sdcard=FileListUtil.getStoragePath(getContext(),false);
        if(sdcard!=null){
            File file=new File(sdcard);
            String mlen="共"+file.listFiles().length+"项";
            TxtFile fileDatas=new TxtFile(file.getName(), sdcard, 0, mlen,true);
            fileDatases.add(fileDatas);
        }
        if(internal!=null){
            File file=new File(internal);
            String mlen="共"+file.listFiles().length+"项";
            TxtFile fileDatas=new TxtFile(file.getName(), internal, 0, mlen,true);
            fileDatases.add(fileDatas);
        }
    }
    private void initView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerviewAdapter=new HRecyclerviewAdapter(getContext(),datas);
        recyclerviewAdapter.setOnItemClickListener(onItemClickListener);
        fileListviewAdapter=new FileListviewAdapter(getContext(),fileDatases);
        fileListviewAdapter.setFileItemClickListener(fileItemClickListener);
        listView.setAdapter(fileListviewAdapter);
        recyclerView.setAdapter(recyclerviewAdapter);
    }
    FileListviewAdapter.FileItemClickListener fileItemClickListener=new FileListviewAdapter.FileItemClickListener() {
        @Override
        public void onClick(int position) {
            File file=new File(fileDatases.get(position).getPath());
            datas.add(file.getName());
            skipPaths.add(file.getPath());
            fileDatases.clear();
            getFileTxt(file);
            getFileDiretory(file);
            viewReflash();
            //fileListviewAdapter.update();

        }
    };
    HRecyclerviewAdapter.OnItemClickListener onItemClickListener=new HRecyclerviewAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            int length=datas.size();
            while (position+1<length){
                datas.remove(position+1);
                skipPaths.remove(position+1);
                length--;
            }
            fileDatases.clear();
            if(position==0){
                FirstInit();
            }else{
                File file=new File(skipPaths.get(position));
                getFileTxt(file);
                getFileDiretory(file);
            }
            viewReflash();
            //fileListviewAdapter.update();

        }
    };
    private void viewReflash(){
        recyclerviewAdapter.refresh();
        fileListviewAdapter=new FileListviewAdapter(getContext(),fileDatases);
        fileListviewAdapter.setFileItemClickListener(fileItemClickListener);
        //setMyChooseListener();
        listView.setAdapter(fileListviewAdapter);
    }
    public void setMyChooseListener(){
        if(fileListviewAdapter!=null){
            Log.i("mythistest","我是"+TAG+"的");
        }
    }
    @Override
    public void onbackForword() {
        if(datas.size()==1){
            getActivity().finish();
        }else if(datas.size()==2){
            datas.remove(datas.size()-1);
            skipPaths.remove(skipPaths.size()-1);
            fileDatases.clear();
            FirstInit();
            viewReflash();
        }else if(datas.size()>2){
            File file=new File(skipPaths.get(skipPaths.size()-2));
            datas.remove(datas.size()-1);
            skipPaths.remove(skipPaths.size()-1);
            fileDatases.clear();
            getFileTxt(file);
            getFileDiretory(file);
            viewReflash();
        }
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(getActivity() instanceof AddXiaoshuoActivity){
            ((AddXiaoshuoActivity)getActivity()).setBackListener(this);
            ((AddXiaoshuoActivity)getActivity()).setInterception(true);
        }
    }
    @Override
    public void onDetach(){
        super.onDetach();
        if(getActivity() instanceof AddXiaoshuoActivity){
            ((AddXiaoshuoActivity)getActivity()).setBackListener(null);
            ((AddXiaoshuoActivity)getActivity()).setInterception(false);
        }
    }

    private void getFileDiretory(File file){
        File[] files=file.listFiles();
        for(File f:files){
            if(f.isDirectory()){
                String mlen="共"+f.listFiles().length+"项";
                TxtFile fileDatas=new TxtFile(f.getName(),f.getPath(),0, mlen,true);
                fileDatases.add(fileDatas);
            }
        }
    }
    private void getFileTxt(File file){
        File[] files=file.listFiles();
        for(File f:files){
            if(!f.isDirectory()){
                if (f.getName().length()>=5){
                    String suffix=f.getName().substring(f.getName().length()-4,f.getName().length());
                    if(suffix.equals(".txt")){
                        TxtFile fileDatas=new TxtFile(f.getName(), f.getPath(), 0, getFileSize(f.length()),false);
                        fileDatases.add(fileDatas);
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

    public void chooseAll(){
        fileListviewAdapter.chooseAll();
    }

    public List<TxtFile> getAdd(){
        return fileListviewAdapter.getAdd();
    }
}
